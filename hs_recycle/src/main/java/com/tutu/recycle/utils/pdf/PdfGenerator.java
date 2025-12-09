package com.tutu.recycle.utils.pdf;

import com.lowagie.text.pdf.BaseFont;
import com.tutu.common.exceptions.ServiceException;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class PdfGenerator {
    public static byte[] htmlToPdf(String htmlContent)  {

        ITextRenderer renderer = new ITextRenderer();

        // -------- 加载中文字体（非常关键，否则中文会乱码） --------
        String fontPath = "/fonts/SimSun.ttf";
        try {
            renderer.getFontResolver().addFont(fontPath, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
        } catch (IOException e) {
            throw new ServiceException(e.getMessage());
        }
        // 设置 HTML 内容
        renderer.setDocumentFromString(htmlContent);
        renderer.layout();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        renderer.createPDF(baos);

        return baos.toByteArray();
    }
}
