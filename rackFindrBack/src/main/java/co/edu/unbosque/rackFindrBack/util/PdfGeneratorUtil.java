package co.edu.unbosque.rackFindrBack.util;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import co.edu.unbosque.rackFindrBack.dto.AuditoriaSesionDTO;
import co.edu.unbosque.rackFindrBack.dto.EstadisticaDTO;
import co.edu.unbosque.rackFindrBack.dto.ResultadoDTO;
import co.edu.unbosque.rackFindrBack.dto.SolicitudDTO;
import co.edu.unbosque.rackFindrBack.dto.UserDTO;

/**
 * Clase utilitaria para la generación dinámica de reportes en formato PDF utilizando la librería OpenPDF.
 * Permite exportar búsquedas individuales, historiales de usuario, listados de cuentas,
 * estadísticas de uso y el reporte administrativo general.
 *
 * @author Equipo RackFindr - Universidad El Bosque
 * @version 1.0
 */
public class PdfGeneratorUtil {

	/**
	 * Genera un archivo PDF en memoria con el detalle de una solicitud individual y las recomendaciones
	 * de las 3 Inteligencias Artificiales.
	 *
	 * @param sol DTO de la solicitud a exportar
	 * @return arreglo de bytes correspondiente al documento PDF generado
	 */
	public static byte[] generarSolicitudIndividualPdf(SolicitudDTO sol) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Document document = new Document(PageSize.A4);

		try {
			PdfWriter.getInstance(document, out);
			document.open();

			Font tituloFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, Color.BLACK);
			Font subtituloFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Color.BLACK);
			Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.DARK_GRAY);
			Font codeFont = FontFactory.getFont(FontFactory.COURIER, 9, Color.BLACK);

			document.add(new Paragraph("Reporte de Busqueda Individual - RackFindr", tituloFont));
			document.add(new Paragraph(" "));

			if (sol == null) {
				document.add(new Paragraph("Solicitud no encontrada.", normalFont));
			} else {
				document.add(new Paragraph("ID Solicitud: " + sol.getId(), normalFont));
				document.add(new Paragraph("Usuario: " + (sol.getUsername() != null ? sol.getUsername() : ""), normalFont));
				document.add(new Paragraph("Fecha: " + (sol.getFecha() != null ? sol.getFecha().toString() : ""), normalFont));
				document.add(new Paragraph("Ecosistema: " + (sol.getEcosistema() != null ? sol.getEcosistema() : ""), normalFont));
				document.add(new Paragraph("Solicitud (Prompt): " + (sol.getPrompt() != null ? sol.getPrompt() : ""), subtituloFont));
				document.add(new Paragraph(" "));
				document.add(new Paragraph("Respuestas de las 3 Inteligencias Artificiales:", subtituloFont));
				document.add(new Paragraph(" "));

				if (sol.getResultados() != null && !sol.getResultados().isEmpty()) {
					PdfPTable table = new PdfPTable(5);
					table.setWidthPercentage(100);
					agregarHeader(table, new String[] { "IA", "Libreria", "Descripcion", "Instalacion", "Ejemplo" });

					for (ResultadoDTO r : sol.getResultados()) {
						table.addCell(new Phrase(r.getIaOrigen() != null ? r.getIaOrigen() : "", normalFont));
						table.addCell(new Phrase(r.getNombre() != null ? r.getNombre() : "", subtituloFont));
						table.addCell(new Phrase(r.getDescripcion() != null ? r.getDescripcion() : "", normalFont));
						table.addCell(new Phrase(r.getInstalacion() != null ? r.getInstalacion() : "", codeFont));
						table.addCell(new Phrase(r.getEjemplo() != null ? r.getEjemplo() : "", codeFont));
					}
					document.add(table);
				} else {
					document.add(new Paragraph(sol.getContenido() != null ? sol.getContenido() : "", codeFont));
				}
			}

			document.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return out.toByteArray();
	}

	public static byte[] generarHistorialUsuarioPdf(String username, String email, List<SolicitudDTO> solicitudes) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Document document = new Document(PageSize.A4);

		try {
			PdfWriter.getInstance(document, out);
			document.open();

			Font tituloFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, Color.BLACK);
			Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.DARK_GRAY);

			document.add(new Paragraph("Historial de Consultas - RackFindr", tituloFont));
			document.add(new Paragraph("Usuario: " + username + " | Fecha: " + LocalDateTime.now().toString(), normalFont));
			document.add(new Paragraph(" "));

			if (solicitudes == null || solicitudes.isEmpty()) {
				document.add(new Paragraph("No hay solicitudes registradas.", normalFont));
			} else {
				PdfPTable table = new PdfPTable(4);
				table.setWidthPercentage(100);
				agregarHeader(table, new String[] { "Fecha", "Ecosistema", "Prompt", "Respuestas (3 IAs)" });

				for (SolicitudDTO sol : solicitudes) {
					table.addCell(sol.getFecha() != null ? sol.getFecha().toString() : "");
					table.addCell(sol.getEcosistema() != null ? sol.getEcosistema() : "");
					table.addCell(sol.getPrompt() != null ? sol.getPrompt() : "");

					StringBuilder resp = new StringBuilder();
					if (sol.getResultados() != null && !sol.getResultados().isEmpty()) {
						for (ResultadoDTO r : sol.getResultados()) {
							resp.append("[").append(r.getIaOrigen() != null ? r.getIaOrigen() : "IA").append("] ")
									.append(r.getNombre()).append(": ")
									.append(r.getDescripcion()).append(" | Instalacion: ")
									.append(r.getInstalacion()).append("\n");
						}
					} else {
						resp.append(sol.getContenido() != null ? sol.getContenido() : "");
					}
					table.addCell(resp.toString());
				}
				document.add(table);
			}

			document.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return out.toByteArray();
	}

	public static byte[] generarReporteUsuariosPdf(List<UserDTO> usuarios) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Document document = new Document(PageSize.A4);

		try {
			PdfWriter.getInstance(document, out);
			document.open();

			Font tituloFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, Color.BLACK);
			Font celdaFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.DARK_GRAY);

			document.add(new Paragraph("Reporte de Usuarios - RackFindr", tituloFont));
			document.add(new Paragraph("Fecha: " + LocalDateTime.now().toString(), celdaFont));
			document.add(new Paragraph(" "));

			PdfPTable userTable = new PdfPTable(5);
			userTable.setWidthPercentage(100);
			agregarHeader(userTable, new String[] { "ID", "Username", "Email", "Rol", "Estado" });

			if (usuarios != null) {
				for (UserDTO u : usuarios) {
					userTable.addCell(new Phrase(String.valueOf(u.getId()), celdaFont));
					userTable.addCell(new Phrase(u.getUsername() != null ? u.getUsername() : "", celdaFont));
					userTable.addCell(new Phrase(u.getEmail() != null ? u.getEmail() : "", celdaFont));
					userTable.addCell(new Phrase(u.getRole() != null ? u.getRole().name() : "USER", celdaFont));
					userTable.addCell(new Phrase(u.isEnabled() ? "ACTIVO" : "INACTIVO", celdaFont));
				}
			}
			document.add(userTable);
			document.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return out.toByteArray();
	}

	public static byte[] generarEstadisticasPdf(EstadisticaDTO stats) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Document document = new Document(PageSize.A4);

		try {
			PdfWriter.getInstance(document, out);
			document.open();

			Font tituloFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, Color.BLACK);
			Font seccionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.BLACK);
			Font celdaFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.DARK_GRAY);

			document.add(new Paragraph("Estadisticas de Uso - RackFindr", tituloFont));
			document.add(new Paragraph("Fecha: " + LocalDateTime.now().toString(), celdaFont));
			document.add(new Paragraph(" "));

			PdfPTable resumen = new PdfPTable(2);
			resumen.setWidthPercentage(100);
			agregarHeader(resumen, new String[] { "Indicador", "Valor" });
			resumen.addCell("Total Usuarios");
			resumen.addCell(String.valueOf(stats.getTotalUsuarios()));
			resumen.addCell("Usuarios Activos / Inactivos");
			resumen.addCell(stats.getUsuariosActivos() + " / " + stats.getUsuariosInactivos());
			resumen.addCell("Administradores / Clientes");
			resumen.addCell(stats.getTotalAdministradores() + " / " + stats.getTotalUsuariosEstandar());
			resumen.addCell("Total Busquedas");
			resumen.addCell(String.valueOf(stats.getTotalSolicitudes()));
			resumen.addCell("Busquedas NPM (Angular)");
			resumen.addCell(String.valueOf(stats.getSolicitudesNpm()));
			resumen.addCell("Busquedas Maven (Spring Boot)");
			resumen.addCell(String.valueOf(stats.getSolicitudesMaven()));
			resumen.addCell("Inicios de Sesion Exitosos / Fallidos");
			resumen.addCell(stats.getIniciosSesionExitosos() + " / " + stats.getIniciosSesionFallidos());
			document.add(resumen);
			document.add(new Paragraph(" "));

			if (stats.getSolicitudesPorUsuario() != null && !stats.getSolicitudesPorUsuario().isEmpty()) {
				document.add(new Paragraph("Busquedas por Usuario", seccionFont));
				PdfPTable porUsuario = new PdfPTable(2);
				porUsuario.setWidthPercentage(100);
				agregarHeader(porUsuario, new String[] { "Usuario", "Cantidad de Busquedas" });
				for (Map.Entry<String, Long> entry : stats.getSolicitudesPorUsuario().entrySet()) {
					porUsuario.addCell(entry.getKey());
					porUsuario.addCell(String.valueOf(entry.getValue()));
				}
				document.add(porUsuario);
			}

			document.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return out.toByteArray();
	}

	public static byte[] generarReporteAdminPdf(EstadisticaDTO stats, List<UserDTO> usuarios,
			List<SolicitudDTO> solicitudes, List<AuditoriaSesionDTO> auditorias) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Document document = new Document(PageSize.A4.rotate());

		try {
			PdfWriter.getInstance(document, out);
			document.open();

			Font tituloFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, Color.BLACK);
			Font seccionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.BLACK);
			Font celdaFont = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.DARK_GRAY);

			document.add(new Paragraph("Reporte Administrativo - RackFindr", tituloFont));
			document.add(new Paragraph("Total usuarios: " + stats.getTotalUsuarios()
					+ " | Activos: " + stats.getUsuariosActivos()
					+ " | Total solicitudes: " + stats.getTotalSolicitudes()
					+ " | NPM: " + stats.getSolicitudesNpm()
					+ " | Maven: " + stats.getSolicitudesMaven()
					+ " | Logins exitosos: " + stats.getIniciosSesionExitosos(), celdaFont));
			document.add(new Paragraph(" "));

			document.add(new Paragraph("Usuarios", seccionFont));
			PdfPTable userTable = new PdfPTable(5);
			userTable.setWidthPercentage(100);
			agregarHeader(userTable, new String[] { "ID", "Username", "Email", "Rol", "Estado" });
			if (usuarios != null) {
				for (UserDTO u : usuarios) {
					userTable.addCell(new Phrase(String.valueOf(u.getId()), celdaFont));
					userTable.addCell(new Phrase(u.getUsername() != null ? u.getUsername() : "", celdaFont));
					userTable.addCell(new Phrase(u.getEmail() != null ? u.getEmail() : "", celdaFont));
					userTable.addCell(new Phrase(u.getRole() != null ? u.getRole().name() : "USER", celdaFont));
					userTable.addCell(new Phrase(u.isEnabled() ? "ACTIVO" : "INACTIVO", celdaFont));
				}
			}
			document.add(userTable);
			document.add(new Paragraph(" "));

			document.add(new Paragraph("Auditoria del Sistema", seccionFont));
			PdfPTable auditTable = new PdfPTable(7);
			auditTable.setWidthPercentage(100);
			agregarHeader(auditTable,
					new String[] { "ID", "Quien (Usuario)", "Que hizo (Accion)", "Cuando (Fecha)", "IP", "Coordenadas", "Ubicacion" });
			if (auditorias != null) {
				for (AuditoriaSesionDTO a : auditorias) {
					auditTable.addCell(new Phrase(String.valueOf(a.getId()), celdaFont));
					auditTable.addCell(new Phrase(a.getUsername() != null ? a.getUsername() : "", celdaFont));
					auditTable.addCell(new Phrase(a.getAccion() != null ? a.getAccion() : "SESION", celdaFont));
					auditTable.addCell(new Phrase(a.getFecha() != null ? a.getFecha().toString() : "", celdaFont));
					auditTable.addCell(new Phrase(a.getIp() != null ? a.getIp() : "", celdaFont));
					auditTable.addCell(new Phrase(a.getCoordenadas() != null ? a.getCoordenadas() : "", celdaFont));
					auditTable.addCell(new Phrase(
							(a.getCiudad() != null ? a.getCiudad() : "") + ", " + (a.getPais() != null ? a.getPais() : ""),
							celdaFont));
				}
			}
			document.add(auditTable);
			document.add(new Paragraph(" "));

			document.add(new Paragraph("Solicitudes Registradas", seccionFont));
			PdfPTable solTable = new PdfPTable(5);
			solTable.setWidthPercentage(100);
			agregarHeader(solTable, new String[] { "ID", "Usuario", "Fecha", "Ecosistema", "Prompt" });
			if (solicitudes != null) {
				for (SolicitudDTO s : solicitudes) {
					solTable.addCell(new Phrase(String.valueOf(s.getId()), celdaFont));
					solTable.addCell(new Phrase(s.getUsername() != null ? s.getUsername() : "", celdaFont));
					solTable.addCell(new Phrase(s.getFecha() != null ? s.getFecha().toString() : "", celdaFont));
					solTable.addCell(new Phrase(s.getEcosistema() != null ? s.getEcosistema() : "", celdaFont));
					solTable.addCell(new Phrase(s.getPrompt() != null ? s.getPrompt() : "", celdaFont));
				}
			}
			document.add(solTable);

			document.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return out.toByteArray();
	}

	private static void agregarHeader(PdfPTable table, String[] headers) {
		for (String h : headers) {
			PdfPCell cell = new PdfPCell(new Phrase(h));
			cell.setBackgroundColor(Color.LIGHT_GRAY);
			table.addCell(cell);
		}
	}

}
