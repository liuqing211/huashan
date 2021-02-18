package com.kedacom.haiou.kmtool.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Field;
import java.util.List;

/**
 * 导出Excel工具类
 * Created by zgming on 2017/12/18.
 */
@Slf4j
public class ExcelExportUtil<T> {

    private Workbook workbook;
    private Sheet sheet;
    private Drawing patriarch;

    //初始化工作区等
    private void init(int rowAccessWindowSize, String sheetName) {
        //建立缓存工作区，当超过rowAccessWindowSize时缓存到磁盘
        workbook = new SXSSFWorkbook(rowAccessWindowSize);
        sheet = workbook.createSheet(sheetName);
        patriarch = sheet.createDrawingPatriarch();
    }

    /**
     * 单元格映射
     */
    public static class CellMap {
        private String title;// 标题
        private String property;// 属性

        public CellMap(String title, String property) {
            this.title = title;
            this.property = property;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getProperty() {
            return property;
        }

        public void setProperty(String property) {
            this.property = property;
        }

    }

    /**
     * 导出Excel
     * @param cellMapList 单元格映射列表
     * @param dataList 数据列表
     * @param rowAccessWindowSize 内存中缓存记录数
     * @param path 保存路径
     * @throws Exception
     */
    public boolean exportSXSSaFExcel(String sheetName, List<CellMap> cellMapList, List<T> dataList, int rowAccessWindowSize, String path) throws Exception {
        init(rowAccessWindowSize, sheetName);
        Row row;
        Cell cell;
        if (cellMapList == null || cellMapList.size() <= 0) {
            throw new Exception("cellMapList不能为空或小于等于0");
        }
        int rowIndex = 0;
        CellStyle contentStyle = createContentStyle();
        row = sheet.createRow(rowIndex++);
        sheet.setDefaultColumnWidth(20);
        int cellSize = cellMapList.size();
        for (int i = 0; i < cellSize; i++) {
            CellMap cellMap = cellMapList.get(i);
            String title = cellMap.getTitle();
            cell = row.createCell(i);
            cell.setCellStyle(createHeadStyle());
            cell.setCellValue(title);
        }
        // 数据
        int rowSize = (dataList == null) ? 0 : dataList.size();
        for (int i = rowIndex; i < rowSize + rowIndex; i++) {
            T t = dataList.get(i - rowIndex);
            Class<?> clazz = t.getClass();
            row = sheet.createRow(i);
            row.setHeightInPoints(72);
            for (int j = 0; j < cellSize; j++) {
                CellMap cellMap = cellMapList.get(j);
                cell = row.createCell(j);
                cell.setCellStyle(contentStyle);
                Object value;
                Field field;
                try {
                    field = clazz.getDeclaredField(cellMap.getProperty());
                    field.setAccessible(true);
                    value = field.get(t);
                } catch (Exception e) {
                    log.error("导出Excel异常: {}", ExceptionUtils.getStackTrace(e));
                    break;
                }
                if (value != null) {
                    if (value instanceof byte[]) {
                        cell = row.createCell(j);
                        cell.setCellStyle(contentStyle);
                        insertPictureByByteArray((byte[])value, "png", cell);
                    }else {
                        cell = row.createCell(j);
                        cell.setCellStyle(contentStyle);
                        RichTextString text = new XSSFRichTextString(String.valueOf(value));
                        cell.setCellValue(text);
                    }
                } else {
                    cell = row.createCell(j);
                    cell.setCellStyle(contentStyle);
                    RichTextString text = new XSSFRichTextString("");
                    cell.setCellValue(text);
                }
            }
        }
        return write2Disk(path);
    }

    private void insertPictureByByteArray(byte[] data, String ext, Cell cell) {
        ByteArrayOutputStream byteArrayOut = null;
        try {
            byteArrayOut = new ByteArrayOutputStream();
            BufferedImage bufferedImg = ImageIO.read(new ByteArrayInputStream(data));
            int imgType = Workbook.PICTURE_TYPE_PNG;
            if (ext.equals("png")) {
                ImageIO.write(bufferedImg, "png", byteArrayOut);
            }
            if (ext.equals("jpg")) {
                ImageIO.write(bufferedImg, "jpg", byteArrayOut);
                imgType = Workbook.PICTURE_TYPE_JPEG;
            }
            byte[] imgBt = byteArrayOut.toByteArray();

            if (cell != null) {
                //Drawing patriarch = sheet.createDrawingPatriarch();
                int r = cell.getRowIndex();
                short c = (short) cell.getColumnIndex();
                XSSFClientAnchor anchor = new XSSFClientAnchor(0, 0, 0, 0, c, r, c, r);
                anchor.setAnchorType(ClientAnchor.AnchorType.DONT_MOVE_AND_RESIZE);
                patriarch.createPicture(anchor, workbook.addPicture(imgBt, imgType)).resize(0.5, 1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (byteArrayOut != null) {
                try {
                    byteArrayOut.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean write2Disk(String path) {
        FileOutputStream out = null;
        try {
            File file = new File(path);
            if (!file.exists()) {
                file.createNewFile();
            }
            out = new FileOutputStream(file);
            workbook.write(out);
        } catch (Exception e) {
            log.error("Excel导出异常: {}", ExceptionUtils.getStackTrace(e));
            return false;
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    private CellStyle createHeadStyle() {
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(HSSFColor.SKY_BLUE.index);
        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        Font font = workbook.createFont();
        font.setColor(HSSFColor.BLACK.index);
        font.setFontHeightInPoints((short) 12);
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        return style;
    }

    private CellStyle createContentStyle() {
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(HSSFColor.WHITE.index);
        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        Font font = workbook.createFont();
        font.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
        style.setFont(font);
        return style;
    }

    private static void createCell(String value, Row row, CellStyle contentStyle, int count) {
        Cell cell = row.createCell(count);
        cell.setCellStyle(contentStyle);
        RichTextString text = new XSSFRichTextString(value);
        cell.setCellValue(text);
    }

    private static Cell creatEmptyCell(Row row, CellStyle style, int count) {
        Cell cell = row.createCell(count);
        cell.setCellStyle(style);
        return cell;
    }

    public static void main(String[] args) {

    }
}
