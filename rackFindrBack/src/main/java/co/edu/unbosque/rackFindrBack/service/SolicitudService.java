package co.edu.unbosque.rackFindrBack.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import co.edu.unbosque.rackFindrBack.dto.ResultadoDTO;
import co.edu.unbosque.rackFindrBack.dto.SolicitudDTO;
import co.edu.unbosque.rackFindrBack.model.Solicitud;
import co.edu.unbosque.rackFindrBack.model.User;
import co.edu.unbosque.rackFindrBack.repository.SolicitudRepository;
import co.edu.unbosque.rackFindrBack.repository.UserRepository;
import co.edu.unbosque.rackFindrBack.util.AESUtil;
import co.edu.unbosque.rackFindrBack.util.PdfGeneratorUtil;

@Service
public class SolicitudService implements CRUDOperation<SolicitudDTO> {

	@Autowired
	private SolicitudRepository solicitudRepo;

	@Autowired
	private UserRepository userRepo;

	@Value("${ai.gemini.api.key:}")
	private String geminiApiKey;

	@Value("${ai.openai.api.key:}")
	private String openaiApiKey;

	@Value("${ai.groq.api.key:}")
	private String groqApiKey;

	private Gson gson = new GsonBuilder().setPrettyPrinting().create();

	public SolicitudService() {
	}

	@Override
	public long count() {
		return solicitudRepo.count();
	}

	@Override
	public boolean exist(Long id) {
		return solicitudRepo.existsById(id) ? true : false;
	}

	@Override
	public int create(SolicitudDTO data) {
		if (data == null || data.getPrompt() == null || data.getPrompt().isEmpty()) {
			return 1;
		}
		procesarYGuardarSolicitud(data);
		return 0;
	}

	public SolicitudDTO procesarYGuardarSolicitud(SolicitudDTO data) {
		String ecosistema = (data.getEcosistema() != null && data.getEcosistema().equalsIgnoreCase("maven")) ? "maven"
				: "npm";
		String promptCliente = data.getPrompt();
		String promptBase = ExternalHTTPRequestHandler.PROMPT_BASE;

		List<ResultadoDTO> recomendaciones = ExternalHTTPRequestHandler.consultarTresIAs(ecosistema, promptCliente,
				geminiApiKey, openaiApiKey, groqApiKey);

		String contenidoJson = gson.toJson(recomendaciones);

		Solicitud entity = new Solicitud();
		entity.setFecha(LocalDateTime.now());
		entity.setEcosistema(ecosistema);
		entity.setPrompt(AESUtil.encrypt(promptCliente));
		entity.setPromptBase(AESUtil.encrypt(promptBase));
		entity.setContenido(AESUtil.encrypt(contenidoJson));
		entity.setIaUsada("Gemini, OpenAI, Llama");

		String username = data.getUsername() != null ? data.getUsername() : "anonimo";
		entity.setUsername(username);
		Optional<User> userOpt = userRepo.findByUsername(username);
		if (userOpt.isPresent()) {
			entity.setUser(userOpt.get());
		}

		Solicitud saved = solicitudRepo.save(entity);
		return mapEntityToDto(saved);
	}

	@Override
	public List<SolicitudDTO> getAll() {
		List<Solicitud> list = solicitudRepo.findAll();
		List<SolicitudDTO> dtoList = new ArrayList<>();
		for (Solicitud s : list) {
			dtoList.add(mapEntityToDto(s));
		}
		return dtoList;
	}

	public List<SolicitudDTO> getByUsername(String username) {
		List<Solicitud> list = solicitudRepo.findByUsernameOrderByFechaDesc(username);
		List<SolicitudDTO> dtoList = new ArrayList<>();
		for (Solicitud s : list) {
			dtoList.add(mapEntityToDto(s));
		}
		return dtoList;
	}

	public SolicitudDTO getById(Long id) {
		Optional<Solicitud> found = solicitudRepo.findById(id);
		if (found.isPresent()) {
			return mapEntityToDto(found.get());
		} else {
			return null;
		}
	}

	@Override
	public int deleteById(Long id) {
		Optional<Solicitud> found = solicitudRepo.findById(id);
		if (found.isPresent()) {
			solicitudRepo.delete(found.get());
			return 0;
		} else {
			return 1;
		}
	}

	@Override
	public int updateById(Long id, SolicitudDTO newData) {
		Optional<Solicitud> found = solicitudRepo.findById(id);
		if (found.isPresent()) {
			Solicitud temp = found.get();
			temp.setEcosistema(newData.getEcosistema());
			temp.setPrompt(AESUtil.encrypt(newData.getPrompt()));
			temp.setContenido(AESUtil.encrypt(newData.getContenido()));
			solicitudRepo.save(temp);
			return 0;
		} else {
			return 1;
		}
	}

	public byte[] exportarHistorialUsuarioPdf(String username) {
		List<SolicitudDTO> historial = getByUsername(username);
		String email = "";
		Optional<User> userOpt = userRepo.findByUsername(username);
		if (userOpt.isPresent() && userOpt.get().getEmail() != null) {
			email = AESUtil.decrypt(userOpt.get().getEmail());
		}
		return PdfGeneratorUtil.generarHistorialUsuarioPdf(username, email, historial);
	}

	private SolicitudDTO mapEntityToDto(Solicitud entity) {
		SolicitudDTO dto = new SolicitudDTO();
		dto.setId(entity.getId());
		dto.setFecha(entity.getFecha());
		dto.setEcosistema(entity.getEcosistema());

		String plainPrompt = AESUtil.decrypt(entity.getPrompt());
		dto.setPrompt(plainPrompt);
		dto.setConsulta(plainPrompt);

		dto.setPromptBase(AESUtil.decrypt(entity.getPromptBase()));

		String plainContenido = AESUtil.decrypt(entity.getContenido());
		dto.setContenido(plainContenido);
		dto.setIaUsada(entity.getIaUsada());
		dto.setUsername(entity.getUsername());
		if (entity.getUser() != null) {
			dto.setUserId(entity.getUser().getId());
		}

		try {
			if (plainContenido != null && plainContenido.trim().startsWith("[")) {
				List<ResultadoDTO> parsed = gson.fromJson(plainContenido, new TypeToken<List<ResultadoDTO>>() {
				}.getType());
				if (parsed != null) {
					dto.setResultados(parsed);
				}
			}
		} catch (Exception e) {
		}

		return dto;
	}

}
