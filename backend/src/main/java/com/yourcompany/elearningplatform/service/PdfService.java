package com.yourcompany.elearningplatform.service;

import com.yourcompany.elearningplatform.entity.Certificate;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

@Service
public class PdfService {

    private static final String CERTIFICATES_DIR = "certificates";

    public String generateCertificatePdf(Certificate certificate) throws IOException {
        // Create certificates directory if it doesn't exist
        File dir = new File(CERTIFICATES_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String fileName = "certificate_" + certificate.getOid() + ".pdf";
        String filePath = CERTIFICATES_DIR + File.separator + fileName;

        // Generate PDF using Apache PDFBox
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.beginText();
                contentStream.setFont(PDType1Font.TIMES_ROMAN, 24);
                contentStream.newLineAtOffset(100, 750);
                contentStream.showText("CERTIFICATE OF COMPLETION");
                contentStream.endText();

                contentStream.beginText();
                contentStream.setFont(PDType1Font.TIMES_ROMAN, 14);
                contentStream.newLineAtOffset(100, 700);
                contentStream.showText("This is to certify that");
                contentStream.endText();

                contentStream.beginText();
                contentStream.setFont(PDType1Font.TIMES_BOLD, 18);
                contentStream.newLineAtOffset(100, 650);
                contentStream.showText(certificate.getStudentName());
                contentStream.endText();

                contentStream.beginText();
                contentStream.setFont(PDType1Font.TIMES_ROMAN, 14);
                contentStream.newLineAtOffset(100, 600);
                contentStream.showText("has successfully completed the course");
                contentStream.endText();

                contentStream.beginText();
                contentStream.setFont(PDType1Font.TIMES_BOLD, 16);
                contentStream.newLineAtOffset(100, 550);
                contentStream.showText(certificate.getCourseName());
                contentStream.endText();

                contentStream.beginText();
                contentStream.setFont(PDType1Font.TIMES_ROMAN, 12);
                contentStream.newLineAtOffset(100, 450);
                contentStream.showText("Certificate OID: " + certificate.getOid());
                contentStream.endText();

                contentStream.beginText();
                contentStream.setFont(PDType1Font.TIMES_ROMAN, 12);
                contentStream.newLineAtOffset(100, 430);
                contentStream.showText("Exam Score: " + certificate.getExamMarks() + " marks");
                contentStream.endText();

                contentStream.beginText();
                contentStream.setFont(PDType1Font.TIMES_ROMAN, 12);
                contentStream.newLineAtOffset(100, 410);
                contentStream.showText("Date of Issue: " + 
                    certificate.getIssueDate().format(DateTimeFormatter.ofPattern("dd MMMM yyyy")));
                contentStream.endText();

                contentStream.beginText();
                contentStream.setFont(PDType1Font.TIMES_ROMAN, 14);
                contentStream.newLineAtOffset(100, 300);
                contentStream.showText("E-Learning Platform");
                contentStream.endText();
            }

            document.save(filePath);
        }

        return filePath;
    }

    public File getCertificatePdf(String pdfPath) {
        File file = new File(pdfPath);
        if (file.exists()) {
            return file;
        }
        return null;
    }
}

