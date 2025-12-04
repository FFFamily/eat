package com.tutu.system.utils;

import lombok.extern.slf4j.Slf4j;
import org.openpdf.text.*;
import org.openpdf.text.pdf.BaseFont;
import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * PDF生成工具类
 * 基于OpenPDF实现
 */
@Slf4j
public class PdfUtils {

    // 中文字体支持
    private static final String FONT_PATH = "/System/Library/Fonts/PingFang.ttc"; // macOS
    private static final String FONT_PATH_WIN = "C:/Windows/Fonts/simsun.ttc"; // Windows
    private static final String FONT_PATH_LINUX = "/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf"; // Linux

    /**
     * 获取中文字体
     */
    private static BaseFont getChineseFont() {
        try {
            // 优先尝试使用系统字体
            if (System.getProperty("os.name").toLowerCase().contains("mac")) {
                return BaseFont.createFont(FONT_PATH + ",0", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
            } else if (System.getProperty("os.name").toLowerCase().contains("win")) {
                return BaseFont.createFont(FONT_PATH_WIN + ",1", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
            } else {
                // Linux系统，尝试使用DejaVu字体
                return BaseFont.createFont(FONT_PATH_LINUX, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
            }
        } catch (Exception e) {
            log.warn("无法加载中文字体，使用默认字体: {}", e.getMessage());
            try {
                // 使用OpenPDF内置字体
                return BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
            } catch (Exception ex) {
                log.error("创建默认字体失败", ex);
                throw new RuntimeException("无法创建字体", ex);
            }
        }
    }

    /**
     * 创建中文字体
     */
    public static Font createChineseFont(float size, int style) {
        BaseFont baseFont = getChineseFont();
        return new Font(baseFont, size, style);
    }

    /**
     * 创建中文字体（默认样式）
     */
    public static Font createChineseFont(float size) {
        return createChineseFont(size, Font.NORMAL);
    }

    /**
     * 生成简单的PDF文档
     * 
     * @param title PDF标题
     * @param content 内容列表（每行一个字符串）
     * @param outputPath 输出文件路径
     * @return 是否生成成功
     */
    public static boolean generateSimplePdf(String title, List<String> content, String outputPath) {
        try {
            Document document = new Document(PageSize.A4);
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(outputPath));
            
            document.open();
            
            // 添加标题
            Font titleFont = createChineseFont(18, Font.BOLD);
            Paragraph titlePara = new Paragraph(title, titleFont);
            titlePara.setAlignment(Element.ALIGN_CENTER);
            titlePara.setSpacingAfter(20);
            document.add(titlePara);
            
            // 添加内容
            Font contentFont = createChineseFont(12);
            for (String line : content) {
                Paragraph para = new Paragraph(line, contentFont);
                para.setSpacingAfter(10);
                document.add(para);
            }
            
            document.close();
            writer.close();
            
            log.info("PDF生成成功: {}", outputPath);
            return true;
        } catch (Exception e) {
            log.error("PDF生成失败: {}", outputPath, e);
            return false;
        }
    }

    /**
     * 生成PDF到字节数组
     * 
     * @param title PDF标题
     * @param content 内容列表
     * @return PDF字节数组
     */
    public static byte[] generatePdfToBytes(String title, List<String> content) {
        try {
            Document document = new Document(PageSize.A4);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            
            document.open();
            
            // 添加标题
            Font titleFont = createChineseFont(18, Font.BOLD);
            Paragraph titlePara = new Paragraph(title, titleFont);
            titlePara.setAlignment(Element.ALIGN_CENTER);
            titlePara.setSpacingAfter(20);
            document.add(titlePara);
            
            // 添加内容
            Font contentFont = createChineseFont(12);
            for (String line : content) {
                Paragraph para = new Paragraph(line, contentFont);
                para.setSpacingAfter(10);
                document.add(para);
            }
            
            document.close();
            writer.close();
            
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("PDF生成失败", e);
            throw new RuntimeException("PDF生成失败", e);
        }
    }

    /**
     * 生成带表格的PDF
     * 
     * @param title PDF标题
     * @param headers 表头
     * @param data 表格数据
     * @param outputPath 输出文件路径
     * @return 是否生成成功
     */
    public static boolean generateTablePdf(String title, String[] headers, List<String[]> data, String outputPath) {
        try {
            Document document = new Document(PageSize.A4);
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(outputPath));
            
            document.open();
            
            // 添加标题
            Font titleFont = createChineseFont(18, Font.BOLD);
            Paragraph titlePara = new Paragraph(title, titleFont);
            titlePara.setAlignment(Element.ALIGN_CENTER);
            titlePara.setSpacingAfter(20);
            document.add(titlePara);
            
            // 创建表格
            PdfPTable table = new PdfPTable(headers.length);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10);
            table.setSpacingAfter(10);
            
            // 设置表头
            Font headerFont = createChineseFont(12, Font.BOLD);
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(8);
                cell.setBackgroundColor(new Color(230, 230, 230));
                table.addCell(cell);
            }
            
            // 添加数据
            Font dataFont = createChineseFont(11);
            for (String[] row : data) {
                for (String cellValue : row) {
                    PdfPCell cell = new PdfPCell(new Phrase(cellValue != null ? cellValue : "", dataFont));
                    cell.setPadding(6);
                    table.addCell(cell);
                }
            }
            
            document.add(table);
            document.close();
            writer.close();
            
            log.info("PDF表格生成成功: {}", outputPath);
            return true;
        } catch (Exception e) {
            log.error("PDF表格生成失败: {}", outputPath, e);
            return false;
        }
    }

    /**
     * 生成带表格的PDF到字节数组
     */
    public static byte[] generateTablePdfToBytes(String title, String[] headers, List<String[]> data) {
        try {
            Document document = new Document(PageSize.A4);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            
            document.open();
            
            // 添加标题
            Font titleFont = createChineseFont(18, Font.BOLD);
            Paragraph titlePara = new Paragraph(title, titleFont);
            titlePara.setAlignment(Element.ALIGN_CENTER);
            titlePara.setSpacingAfter(20);
            document.add(titlePara);
            
            // 创建表格
            PdfPTable table = new PdfPTable(headers.length);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10);
            table.setSpacingAfter(10);
            
            // 设置表头
            Font headerFont = createChineseFont(12, Font.BOLD);
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(8);
                cell.setBackgroundColor(new Color(230, 230, 230));
                table.addCell(cell);
            }
            
            // 添加数据
            Font dataFont = createChineseFont(11);
            for (String[] row : data) {
                for (String cellValue : row) {
                    PdfPCell cell = new PdfPCell(new Phrase(cellValue != null ? cellValue : "", dataFont));
                    cell.setPadding(6);
                    table.addCell(cell);
                }
            }
            
            document.add(table);
            document.close();
            writer.close();
            
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("PDF表格生成失败", e);
            throw new RuntimeException("PDF表格生成失败", e);
        }
    }

    /**
     * 保存PDF字节数组到文件
     */
    public static boolean savePdfToFile(byte[] pdfBytes, String outputPath) {
        try {
            Path path = Paths.get(outputPath);
            Files.createDirectories(path.getParent());
            Files.write(path, pdfBytes);
            log.info("PDF保存成功: {}", outputPath);
            return true;
        } catch (IOException e) {
            log.error("PDF保存失败: {}", outputPath, e);
            return false;
        }
    }
}

