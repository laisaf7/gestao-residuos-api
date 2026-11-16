package com.esg.residuos.service;

import com.esg.residuos.domain.entity.Artigo;
import com.esg.residuos.dto.request.ArtigoRequest;
import com.esg.residuos.dto.response.ArtigoResponse;
import com.esg.residuos.exception.ResourceNotFoundException;
import com.esg.residuos.repository.ArtigoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArtigoService {

    private final ArtigoRepository repository;

    public List<ArtigoResponse> listar(String material) {
        return material == null
                ? repository.listarSemImagem()
                : repository.listarSemImagemPorMaterial(material);
    }

    public byte[] imagem(Long id) {
        return repository.buscarImagem(id)
                .orElseThrow(() -> new ResourceNotFoundException("Este artigo nao tem foto"));
    }

    @Transactional
    public ArtigoResponse criar(String autor, ArtigoRequest request) {
        Artigo artigo = repository.save(Artigo.builder()
                .titulo(request.getTitulo().trim())
                .material(request.getMaterial())
                .conteudo(request.getConteudo().trim())
                .autor(autor)
                .imagem(lerJpeg(request.getImagemBase64()))
                .build());
        return toResponse(artigo);
    }

    @Transactional
    public ArtigoResponse editar(Long id, String usuario, ArtigoRequest request) {
        Artigo artigo = doAutor(id, usuario);
        artigo.setTitulo(request.getTitulo().trim());
        artigo.setMaterial(request.getMaterial());
        artigo.setConteudo(request.getConteudo().trim());
        byte[] novaFoto = lerJpeg(request.getImagemBase64());
        if (novaFoto != null) artigo.setImagem(novaFoto);
        else if (request.isRemoverImagem()) artigo.setImagem(null);
        artigo.setDataAtualizacao(LocalDateTime.now());
        return toResponse(artigo);
    }

    /** Autor exclui o proprio artigo; admin exclui qualquer um (moderacao). */
    @Transactional
    public void excluir(Long id, String usuario, boolean admin) {
        repository.delete(admin ? buscar(id) : doAutor(id, usuario));
    }

    /** So quem publicou pode editar ou excluir. */
    private Artigo doAutor(Long id, String usuario) {
        Artigo artigo = buscar(id);
        if (!artigo.getAutor().equals(usuario)) {
            throw new AccessDeniedException("Apenas quem publicou pode alterar este artigo");
        }
        return artigo;
    }

    private Artigo buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Artigo nao encontrado"));
    }

    private ArtigoResponse toResponse(Artigo a) {
        return ArtigoResponse.builder()
                .id(a.getId())
                .titulo(a.getTitulo())
                .material(a.getMaterial())
                .conteudo(a.getConteudo())
                .autor(a.getAutor())
                .dataCriacao(a.getDataCriacao())
                .dataAtualizacao(a.getDataAtualizacao())
                .temImagem(a.getImagem() != null)
                .build();
    }

    /** Aceita so JPEG: e o que o app envia e o que a rota de imagem declara como Content-Type. */
    static byte[] lerJpeg(String base64) {
        if (base64 == null || base64.isBlank()) return null;
        byte[] bytes;
        try {
            bytes = Base64.getDecoder().decode(base64);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("A foto enviada esta corrompida");
        }
        boolean jpeg = bytes.length > 3 && (bytes[0] & 0xFF) == 0xFF && (bytes[1] & 0xFF) == 0xD8 && (bytes[2] & 0xFF) == 0xFF;
        if (!jpeg) throw new IllegalArgumentException("A foto deve estar no formato JPEG");
        return bytes;
    }
}
