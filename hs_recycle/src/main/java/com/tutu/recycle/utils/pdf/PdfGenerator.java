package com.tutu.recycle.utils.pdf;

import com.lowagie.text.pdf.BaseFont;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;

public class PdfGenerator {
    public static byte[] htmlToPdf(String htmlContent) throws Exception {

        ITextRenderer renderer = new ITextRenderer();

        // -------- 加载中文字体（非常关键，否则中文会乱码） --------
        String fontPath = "/fonts/SimSun.ttf"; // 放 resources/fonts/simsun.ttf
        renderer.getFontResolver().addFont(fontPath, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);

        // 设置 HTML 内容
        renderer.setDocumentFromString(htmlContent);
        renderer.layout();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        renderer.createPDF(baos);

        return baos.toByteArray();
    }
}
