package co.edu.unbosque.rackFindrBack.service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import co.edu.unbosque.rackFindrBack.dto.ResultadoDTO;

public class ExternalHTTPRequestHandler {

	private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2)
			.connectTimeout(Duration.ofSeconds(10)).build();

	public static final String PROMPT_BASE = "Recomienda librerias de desarrollo para el ecosistema indicado. "
			+ "Responde en formato JSON array con los campos: nombre, descripcion, instalacion, comoUsar, ejemplo.";

	public static String doGet(String url) {
		HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(url))
				.header("Content-Type", "application/json").build();

		HttpResponse<String> response = null;
		try {
			response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		System.out.println("status code -> " + response.statusCode());

		String uglyJson = response.body();
		return prettyPrintUsingGson(uglyJson);
	}

	public static String doPost(String url, String json) {
		HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(json))
				.uri(URI.create(url)).header("Content-Type", "application/json").build();

		HttpResponse<String> response = null;
		try {
			response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("status code -> " + response.statusCode());

		return prettyPrintUsingGson(response.body());
	}

	public static String doPut(String url, String json) {
		HttpRequest request = HttpRequest.newBuilder().PUT(HttpRequest.BodyPublishers.ofString(json))
				.uri(URI.create(url)).setHeader("User-Agent", "Java 11 HttpClient Bot")
				.header("Content-Type", "application/json").build();

		HttpResponse<String> response = null;
		try {
			response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("status code -> " + response.statusCode());
		System.out.println(response.body());
		return prettyPrintUsingGson(response.body());
	}

	public static String doDelete(String url) {
		HttpRequest request = HttpRequest.newBuilder().DELETE().uri(URI.create(url))
				.setHeader("User-Agent", "Java 11 HttpClient Bot")
				.header("Content-Type", "application/json").build();

		HttpResponse<String> response = null;
		try {
			response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("status code -> " + response.statusCode());

		return prettyPrintUsingGson(response.body());
	}

	public static String prettyPrintUsingGson(String uglyJson) {
		Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().create();
		JsonElement jsonElement = JsonParser.parseString(uglyJson);
		String prettyJsonString = gson.toJson(jsonElement);
		return prettyJsonString;
	}

	public static List<ResultadoDTO> consultarTresIAs(String ecosistema, String promptCliente, String geminiKey,
			String openaiKey, String groqKey) {
		String eco = (ecosistema != null && ecosistema.equalsIgnoreCase("maven")) ? "maven" : "npm";
		String promptCompleto = PROMPT_BASE + " Ecosistema: " + eco + ". Solicitud: " + promptCliente;

		List<ResultadoDTO> lista = new ArrayList<>();
		lista.addAll(consultarIA1Gemini(promptCompleto, geminiKey, eco));
		lista.addAll(consultarIA2OpenAI(promptCompleto, openaiKey, eco));
		lista.addAll(consultarIA3Llama(promptCompleto, groqKey, eco));
		return lista;
	}

	public static List<ResultadoDTO> consultarIA1Gemini(String promptCompleto, String apiKey, String ecosistema) {
		if (apiKey != null && !apiKey.isEmpty()) {
			try {
				String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key="
						+ apiKey;
				String body = "{\"contents\":[{\"parts\":[{\"text\":\"" + promptCompleto.replace("\"", "'") + "\"}]}]}";
				HttpRequest request = HttpRequest.newBuilder()
						.POST(HttpRequest.BodyPublishers.ofString(body))
						.uri(URI.create(url))
						.header("Content-Type", "application/json")
						.build();
				HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
				if (response.statusCode() == 200) {
					JsonObject root = JsonParser.parseString(response.body()).getAsJsonObject();
					String text = root.getAsJsonArray("candidates").get(0).getAsJsonObject()
							.getAsJsonObject("content").getAsJsonArray("parts").get(0).getAsJsonObject()
							.get("text").getAsString();
					List<ResultadoDTO> res = parsearLista(text, "Gemini");
					if (!res.isEmpty()) {
						return res;
					}
				}
			} catch (Exception e) {
				System.out.println("Error en Gemini API: " + e.getMessage());
			}
		}
		return datosPorDefecto(ecosistema, "Gemini", 1);
	}

	public static List<ResultadoDTO> consultarIA2OpenAI(String promptCompleto, String apiKey, String ecosistema) {
		if (apiKey != null && !apiKey.isEmpty()) {
			try {
				String url = "https://api.openai.com/v1/chat/completions";
				String body = "{\"model\":\"gpt-4o-mini\",\"messages\":[{\"role\":\"user\",\"content\":\""
						+ promptCompleto.replace("\"", "'") + "\"}]}";
				HttpRequest request = HttpRequest.newBuilder()
						.POST(HttpRequest.BodyPublishers.ofString(body))
						.uri(URI.create(url))
						.header("Content-Type", "application/json")
						.header("Authorization", "Bearer " + apiKey)
						.build();
				HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
				if (response.statusCode() == 200) {
					JsonObject root = JsonParser.parseString(response.body()).getAsJsonObject();
					String text = root.getAsJsonArray("choices").get(0).getAsJsonObject()
							.getAsJsonObject("message").get("content").getAsString();
					List<ResultadoDTO> res = parsearLista(text, "OpenAI");
					if (!res.isEmpty()) {
						return res;
					}
				}
			} catch (Exception e) {
				System.out.println("Error en OpenAI API: " + e.getMessage());
			}
		}
		return datosPorDefecto(ecosistema, "OpenAI", 2);
	}

	public static List<ResultadoDTO> consultarIA3Llama(String promptCompleto, String apiKey, String ecosistema) {
		try {
			String encoded = URLEncoder.encode(promptCompleto, StandardCharsets.UTF_8);
			String url = "https://text.pollinations.ai/" + encoded;
			HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(url)).build();
			HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
			if (response.statusCode() == 200 && response.body() != null) {
				List<ResultadoDTO> res = parsearLista(response.body(), "Llama");
				if (!res.isEmpty()) {
					return res;
				}
			}
		} catch (Exception e) {
			System.out.println("Error en Llama API: " + e.getMessage());
		}
		return datosPorDefecto(ecosistema, "Llama", 3);
	}

	private static List<ResultadoDTO> parsearLista(String texto, String iaOrigen) {
		List<ResultadoDTO> lista = new ArrayList<>();
		try {
			int inicio = texto.indexOf('[');
			int fin = texto.lastIndexOf(']');
			if (inicio != -1 && fin != -1 && fin > inicio) {
				String jsonArr = texto.substring(inicio, fin + 1);
				JsonArray arr = JsonParser.parseString(jsonArr).getAsJsonArray();
				for (JsonElement el : arr) {
					JsonObject obj = el.getAsJsonObject();
					String nombre = obj.has("nombre") ? obj.get("nombre").getAsString() : "Libreria";
					String desc = obj.has("descripcion") ? obj.get("descripcion").getAsString() : "";
					String inst = obj.has("instalacion") ? obj.get("instalacion").getAsString() : "";
					String uso = obj.has("comoUsar") ? obj.get("comoUsar").getAsString() : "";
					String ej = obj.has("ejemplo") ? obj.get("ejemplo").getAsString() : "";
					lista.add(new ResultadoDTO(nombre, desc, inst, uso, ej, iaOrigen));
				}
			}
		} catch (Exception e) {
			return lista;
		}
		return lista;
	}

	private static List<ResultadoDTO> datosPorDefecto(String ecosistema, String iaOrigen, int num) {
		List<ResultadoDTO> lista = new ArrayList<>();
		if ("maven".equalsIgnoreCase(ecosistema)) {
			if (num == 1) {
				lista.add(new ResultadoDTO("Spring Boot Starter Validation",
						"Valida los datos que llegan al backend.",
						"<dependency>\n  <groupId>org.springframework.boot</groupId>\n  <artifactId>spring-boot-starter-validation</artifactId>\n</dependency>",
						"Agrega anotaciones como @NotBlank a los campos de tu clase y @Valid al parametro del controlador.",
						"@NotBlank private String nombre;", iaOrigen));
			} else if (num == 2) {
				lista.add(new ResultadoDTO("Lombok",
						"Reduce el codigo repetitivo de las clases Java.",
						"<dependency>\n  <groupId>org.projectlombok</groupId>\n  <artifactId>lombok</artifactId>\n</dependency>",
						"Escribe @Data encima de la clase y Lombok genera los getters, setters y constructores por ti.",
						"@Data public class Usuario { private String nombre; }", iaOrigen));
			} else {
				lista.add(new ResultadoDTO("ModelMapper",
						"Convierte facilmente entre entidades y DTOs en Spring Boot.",
						"<dependency>\n  <groupId>org.modelmapper</groupId>\n  <artifactId>modelmapper</artifactId>\n  <version>3.1.1</version>\n</dependency>",
						"Crea un Bean de ModelMapper e inyectalo en el servicio para mapear objetos.",
						"UserDTO dto = modelMapper.map(entity, UserDTO.class);", iaOrigen));
			}
		} else {
			if (num == 1) {
				lista.add(new ResultadoDTO("GSAP",
						"Libreria potente para animar cualquier elemento.",
						"npm install gsap",
						"Importa gsap en tu componente y llama a gsap.to() indicando el elemento y la animacion.",
						"gsap.to('.caja', { x: 100, duration: 1 });", iaOrigen));
			} else if (num == 2) {
				lista.add(new ResultadoDTO("@angular/animations",
						"Animaciones integradas en Angular.",
						"npm install @angular/animations",
						"Define un trigger con sus estados dentro del decorador @Component y asocialo a un elemento del HTML.",
						"trigger('abrir', [state('cerrado', style({ height: 0 }))])", iaOrigen));
			} else {
				lista.add(new ResultadoDTO("SweetAlert2",
						"Muestra alertas y modales personalizados en aplicaciones web.",
						"npm install sweetalert2",
						"Importa Swal desde sweetalert2 y llama al metodo Swal.fire() con el mensaje.",
						"Swal.fire('Exito', 'Operacion realizada', 'success');", iaOrigen));
			}
		}
		return lista;
	}

	public static Map<String, String> obtenerGeolocalizacionIp(String ipCliente) {
		Map<String, String> datos = new HashMap<>();
		datos.put("ip", (ipCliente != null && !ipCliente.isEmpty()) ? ipCliente : "127.0.0.1");
		datos.put("coordenadas", "4.6097, -74.0817");
		datos.put("ciudad", "Bogota");
		datos.put("pais", "Colombia");

		try {
			String ipConsulta = (ipCliente == null || ipCliente.equals("127.0.0.1")
					|| ipCliente.equals("0:0:0:0:0:0:0:1")) ? "" : ipCliente;
			String url = "http://ip-api.com/json/" + ipConsulta;
			HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(url)).build();
			HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
			if (response.statusCode() == 200) {
				JsonObject obj = JsonParser.parseString(response.body()).getAsJsonObject();
				if (obj.has("status") && "success".equals(obj.get("status").getAsString())) {
					datos.put("ip", obj.get("query").getAsString());
					datos.put("coordenadas", obj.get("lat").getAsDouble() + ", " + obj.get("lon").getAsDouble());
					datos.put("ciudad", obj.get("city").getAsString());
					datos.put("pais", obj.get("country").getAsString());
				}
			}
		} catch (Exception e) {
			System.out.println("No se pudo consultar geolocalizacion: " + e.getMessage());
		}
		return datos;
	}

}
