package com.esg.residuos.service;

import com.esg.residuos.domain.entity.PontoColeta;
import com.esg.residuos.domain.entity.Produto;
import com.esg.residuos.domain.entity.TransacaoMoeda;
import com.esg.residuos.domain.entity.Usuario;
import com.esg.residuos.domain.enums.TipoTransacao;
import com.esg.residuos.dto.request.ProdutoRequest;
import com.esg.residuos.dto.response.ProdutoResponse;
import com.esg.residuos.dto.response.TransacaoResponse;
import com.esg.residuos.exception.BusinessException;
import com.esg.residuos.exception.ResourceNotFoundException;
import com.esg.residuos.repository.PontoColetaRepository;
import com.esg.residuos.repository.ProdutoRepository;
import com.esg.residuos.repository.TransacaoMoedaRepository;
import com.esg.residuos.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/** Catalogo de produtos dos artesaos e compra com EcoMoedas. */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProdutoService {

    private final ProdutoRepository repository;
    private final PontoColetaRepository pontoRepository;
    private final UsuarioRepository usuarioRepository;
    private final TransacaoMoedaRepository transacaoRepository;

    public List<ProdutoResponse> listar() {
        return repository.listarSemImagem();
    }

    public byte[] imagem(Long id) {
        return repository.buscarImagem(id)
                .orElseThrow(() -> new ResourceNotFoundException("Este produto nao tem foto"));
    }

    @Transactional
    public ProdutoResponse criar(String artesao, ProdutoRequest request) {
        PontoColeta atelie = pontoRepository.findByArtesaoUsernameIgnoreCase(artesao)
                .orElseThrow(() -> new AccessDeniedException("Apenas artesaos podem publicar produtos"));
        Produto produto = Produto.builder().atelie(atelie).build();
        preencher(produto, request);
        return toResponse(repository.save(produto));
    }

    @Transactional
    public ProdutoResponse editar(Long id, String usuario, ProdutoRequest request) {
        Produto produto = doArtesao(id, usuario);
        preencher(produto, request);
        produto.setDataAtualizacao(LocalDateTime.now());
        return toResponse(produto);
    }

    /** Artesao exclui o proprio produto; admin exclui qualquer um (moderacao). */
    @Transactional
    public void excluir(Long id, String usuario, boolean admin) {
        repository.delete(admin ? buscar(id) : doArtesao(id, usuario));
    }

    /** Debita o comprador e credita o artesao; devolve a transacao do comprador com o codigo de retirada. */
    @Transactional
    public TransacaoResponse comprar(Long id, String comprador) {
        Produto produto = buscar(id);
        BigDecimal preco = produto.getPrecoMoedas();
        if (preco == null) {
            throw new BusinessException("Este produto e vendido so em reais, direto com o artesao");
        }
        String vendedor = produto.getAtelie().getArtesao().getUsername();
        if (vendedor.equalsIgnoreCase(comprador)) {
            throw new BusinessException("Voce nao pode comprar o proprio produto");
        }
        // Trava as duas carteiras sempre na mesma ordem, para duas compras cruzadas nao se bloquearem
        boolean compradorPrimeiro = comprador.compareToIgnoreCase(vendedor) < 0;
        Usuario primeiro = travar(compradorPrimeiro ? comprador : vendedor);
        Usuario segundo = travar(compradorPrimeiro ? vendedor : comprador);
        Usuario quemCompra = compradorPrimeiro ? primeiro : segundo;
        Usuario quemVende = compradorPrimeiro ? segundo : primeiro;

        if (quemCompra.getSaldoEcoMoedas().compareTo(preco) < 0) {
            throw new BusinessException("Saldo insuficiente");
        }
        quemCompra.setSaldoEcoMoedas(quemCompra.getSaldoEcoMoedas().subtract(preco));
        quemVende.setSaldoEcoMoedas(quemVende.getSaldoEcoMoedas().add(preco));

        String codigo = UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
        transacaoRepository.save(TransacaoMoeda.builder()
                .usuario(quemVende)
                .tipo(TipoTransacao.VENDA)
                .valor(preco)
                .descricao(resumir("Venda: " + produto.getNome() + " (retirada " + codigo + ")"))
                .build());
        TransacaoMoeda compra = transacaoRepository.save(TransacaoMoeda.builder()
                .usuario(quemCompra)
                .tipo(TipoTransacao.COMPRA)
                .valor(preco)
                .descricao(resumir("Compra: " + produto.getNome() + " em " + produto.getAtelie().getNome()))
                .codigoVoucher(codigo)
                .build());
        return TransacaoResponse.builder()
                .id(compra.getId())
                .tipo(compra.getTipo())
                .valor(compra.getValor())
                .descricao(compra.getDescricao())
                .codigoVoucher(compra.getCodigoVoucher())
                .dataTransacao(compra.getDataTransacao())
                .build();
    }

    private void preencher(Produto produto, ProdutoRequest request) {
        produto.setNome(request.getNome().trim());
        produto.setDescricao(request.getDescricao() == null || request.getDescricao().isBlank()
                ? null : request.getDescricao().trim());
        produto.setPrecoReais(request.getPrecoReais());
        produto.setPrecoMoedas(request.getPrecoMoedas());
        byte[] novaFoto = ArtigoService.lerJpeg(request.getImagemBase64());
        if (novaFoto != null) produto.setImagem(novaFoto);
        else if (request.isRemoverImagem()) produto.setImagem(null);
    }

    /** So o artesao dono do atelie pode editar ou excluir. */
    private Produto doArtesao(Long id, String usuario) {
        Produto produto = buscar(id);
        if (!produto.getAtelie().getArtesao().getUsername().equalsIgnoreCase(usuario)) {
            throw new AccessDeniedException("Apenas o artesao que publicou pode alterar este produto");
        }
        return produto;
    }

    private Produto buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto nao encontrado"));
    }

    private Usuario travar(String username) {
        return usuarioRepository.findForUpdateByUsernameIgnoreCase(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario nao encontrado"));
    }

    /** DESCRICAO de TRANSACAO_MOEDA tem 200 caracteres. */
    private static String resumir(String texto) {
        return texto.length() <= 200 ? texto : texto.substring(0, 197) + "...";
    }

    private ProdutoResponse toResponse(Produto p) {
        PontoColeta a = p.getAtelie();
        return ProdutoResponse.builder()
                .id(p.getId())
                .nome(p.getNome())
                .descricao(p.getDescricao())
                .precoReais(p.getPrecoReais())
                .precoMoedas(p.getPrecoMoedas())
                .idPontoColeta(a.getId())
                .nomeAtelie(a.getNome())
                .bairro(a.getBairro())
                .endereco(a.getEndereco())
                .material(a.getTipoResiduo().getNome())
                .artesao(a.getArtesao().getUsername())
                .dataCriacao(p.getDataCriacao())
                .dataAtualizacao(p.getDataAtualizacao())
                .temImagem(p.getImagem() != null)
                .build();
    }
}
