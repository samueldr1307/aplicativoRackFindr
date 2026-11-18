package co.edu.unbosque.rackFindrBack.util;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.List;

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

public class PdfGeneratorUtil {

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
				table.addCell("Fecha");
				table.addCell("Ecosistema");
				table.addCell("Prompt");
				table.addCell("Respuesta");

				for (SolicitudDTO sol : solicitudes) {
					table.addCell(sol.getFecha() != null ? sol.getFecha().toString() : "");
					table.addCell(sol.getEcosistema() != null ? sol.getEcosistema() : "");
					table.addCell(sol.getPrompt() != null ? sol.getPrompt() : "");

					StringBuilder resp = new StringBuilder();
					if (sol.getResultados() != null && !sol.getResultados().isEmpty()) {
						for (ResultadoDTO r : sol.getResultados()) {
							resp.append("- ").append(r.getNombre()).append(": ")
									.append(r.getDescripcion()).append(" (")
									.append(r.getInstalacion()).append(")\n");
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
					+ " | Total solicitudes: " + stats.getTotalSolicitudes()
					+ " | NPM: " + stats.getSolicitudesNpm()
					+ " | Maven: " + stats.getSolicitudesMaven()
					+ " | Logins exitosos: " + stats.getIniciosSesionExitosos(), celdaFont));
			document.add(new Paragraph(" "));

			document.add(new Paragraph("Usuarios", seccionFont));
			PdfPTable userTable = new PdfPTable(4);
			userTable.setWidthPercentage(100);
			agregarHeader(userTable, new String[] { "ID", "Username", "Email", "Rol" });
			if (usuarios != null) {
				for (UserDTO u : usuarios) {
					userTable.addCell(new Phrase(String.valueOf(u.getId()), celdaFont));
					userTable.addCell(new Phrase(u.getUsername() != null ? u.getUsername() : "", celdaFont));
					userTable.addCell(new Phrase(u.getEmail() != null ? u.getEmail() : "", celdaFont));
					userTable.addCell(new Phrase(u.getRole() != null ? u.getRole().name() : "USER", celdaFont));
				}
			}
			document.add(userTable);
			document.add(new Paragraph(" "));

			document.add(new Paragraph("Auditoria de Inicios de Sesion", seccionFont));
			PdfPTable auditTable = new PdfPTable(6);
			auditTable.setWidthPercentage(100);
			agregarHeader(auditTable, new String[] { "ID", "Usuario", "Fecha", "IP", "Coordenadas", "Exitoso" });
			if (auditorias != null) {
				for (AuditoriaSesionDTO a : auditorias) {
					auditTable.addCell(new Phrase(String.valueOf(a.getId()), celdaFont));
					auditTable.addCell(new Phrase(a.getUsername() != null ? a.getUsername() : "", celdaFont));
					auditTable.addCell(new Phrase(a.getFecha() != null ? a.getFecha().toString() : "", celdaFont));
					auditTable.addCell(new Phrase(a.getIp() != null ? a.getIp() : "", celdaFont));
					auditTable.addCell(new Phrase(a.getCoordenadas() != null ? a.getCoordenadas() : "", celdaFont));
					auditTable.addCell(new Phrase(a.isExitoso() ? "SI" : "NO", celdaFont));
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
