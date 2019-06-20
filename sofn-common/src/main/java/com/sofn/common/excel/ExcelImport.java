package com.sofn.common.excel;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.sofn.common.excel.annotation.ExcelField;
import com.sofn.common.excel.util.ExcelReflectionUtil;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Excel导入工具类
 * Created by heyongjie on 2019/6/13 13:53
 */
public class ExcelImport implements Closeable {

    private static Logger log = LoggerFactory.getLogger(ExcelImport.class);

    /**
     * 工作薄对象
     */
    private Workbook wb;

    /**
     * 工作表对象
     */
    private Sheet sheet;

    /**
     * 标题行数
     */
    private int headerNum;

    /**
     * 用于清理缓存
     */
    private Set<Class<?>> fieldTypes = Sets.newHashSet();

    /**
     * 构造函数
     *
     * @param file      需要读取的文件
     * @param headerNum 从哪行开始读取   从0开始计算 和Excel中的不一样
     * @throws Exception
     */
    public ExcelImport(File file, int headerNum)
            throws Exception {
        this(file, headerNum, 0);
    }

    /**
     * 构造函数
     *
     * @param file             需要读取的文件
     * @param headerNum        从哪行开始读取
     * @param sheetIndexOrName Sheet位置
     * @throws Exception
     */
    public ExcelImport(File file, int headerNum, Object sheetIndexOrName)
            throws Exception {
        this(file.getName(), new FileInputStream(file), headerNum, sheetIndexOrName);
    }

    /**
     * 构造行数
     *
     * @param fileName         文件名称
     * @param is               文件流
     * @param headerNum        从哪行开始读取
     * @param sheetIndexOrName Sheet位置
     * @throws InvalidFormatException
     * @throws IOException
     */
    public ExcelImport(String fileName, InputStream is, int headerNum, Object sheetIndexOrName)
            throws InvalidFormatException, IOException {
        if (StringUtils.isBlank(fileName)) {
            throw new ExcelException("导入文档为空!");
        } else if (fileName.toLowerCase().endsWith("xls")) {
            this.wb = new HSSFWorkbook(is);
        } else if (fileName.toLowerCase().endsWith("xlsx")) {
            this.wb = new XSSFWorkbook(is);
        } else {
            throw new ExcelException("文档格式不正确!");
        }
        this.setSheet(sheetIndexOrName, headerNum);
        log.debug("Initialize success.");
    }


    /**
     * 设置当前工作表和标题行数
     *
     * @author ThinkGem
     */
    public void setSheet(Object sheetIndexOrName, int headerNum) {
        if (sheetIndexOrName instanceof Integer || sheetIndexOrName instanceof Long) {
            this.sheet = this.wb.getSheetAt(Integer.parseInt(sheetIndexOrName + ""));
        } else {
            this.sheet = this.wb.getSheet(ObjectUtils.toString(sheetIndexOrName));
        }
        if (this.sheet == null) {
            throw new ExcelException("没有找到‘" + sheetIndexOrName + "’工作表!");
        }
        this.headerNum = headerNum;
    }

    /**
     * 获取行对象
     *
     * @param rownum
     * @return 返回Row对象，如果空行返回null
     */
    public Row getRow(int rownum) {
        Row row = this.sheet.getRow(rownum);
        if (row == null) {
            return null;
        }
        // 验证是否是空行，如果空行返回null
        short cellNum = 0;
        short emptyNum = 0;
        Iterator<Cell> it = row.cellIterator();
        while (it.hasNext()) {
            cellNum++;
            Cell cell = it.next();
            if (StringUtils.isBlank(cell.toString())) {
                emptyNum++;
            }
        }
        if (cellNum == emptyNum) {
            return null;
        }
        return row;
    }

    /**
     * 获取开始读取位置
     *
     * @return
     */
    public int getDataRowNum() {
        return headerNum;
    }

    /**
     * 获取最后一个数据行号
     *
     * @return
     */
    public int getLastDataRowNum() {
        return this.sheet.getLastRowNum() + 1;
    }

    /**
     * 获取导入数据列表
     *
     * @param cls 导入对象类型
     */
    public <E> List<E> getDataList(Class<E> cls, ExcelField.Type type) throws ExcelException {
        // 读取Excel获取数据
        List<E> dataList = Lists.newArrayList();
        for (int i = this.getDataRowNum(); i < this.getLastDataRowNum(); i++) {
            // 行
            Row row = this.getRow(i);
            if (row == null) {
                continue;
            }
            try {
                Object obj = ExcelReflectionUtil.setValueByRowAndClass(row, cls, 0,type);
                dataList.add((E) obj);
            } catch (Exception ex) {
                ex.printStackTrace();
                throw new ExcelException(ex.getMessage());
            }
        }
        return dataList;
    }


    @Override
    public void close() {
        Iterator<Class<?>> it = fieldTypes.iterator();
        while (it.hasNext()) {
            Class<?> clazz = it.next();
            try {
                clazz.getMethod("clearCache").invoke(null);
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
    }

    /**
     * 导入文件获取数据
     * @param filePath    文件路径  C:\Users\heyongjie\Desktop\testReadExcel.xlsx
     * @param headerNum   如果使用的是导出模板然后在导入请传入-1
     * @param cls         Excel注解标志类
     * @param <T>         泛型
     * @return            读取Excel后产生的数据
     */
    public static <T> List<T>  getDataByExcel(String filePath, int headerNum, Class<T> cls) throws ExcelException{
        // 默认读第一个Sheet
        List<T> data;
        try{
            data =  getDataByExcelAndSheet(filePath,headerNum,cls, ExcelField.Type.IMPORT,0);
        }catch (ExcelException e){
            throw  new ExcelException(e.getMessage());
        }
        return data;
    }


    /**
     * 根据上传的文件获取数据
     * @param multipartFile  上传的文件
     * @param cls  Excel注解类
     * @param <T>  泛型
     * @return  根据上传的文件获取的数据
     * @throws ExcelException  读取文件异常
     */
    public static <T> List<T> getDataByExcel(MultipartFile multipartFile, Class<T> cls) throws ExcelException{
        return getDataByExcel(multipartFile,-1,cls,null);
    }

    /**
     * 根据上传的文件获取数据
     * @param multipartFile  上传的文件
     * @param headerNum    从哪行开始读取
     * @param cls     Excel注解类
     * @param sheetIndexOrName   读取第几个sheet 默认第0个
     * @param <T>  泛型
     * @return  根据上传的文件获取的数据
     * @throws ExcelException  读取文件异常
     */
    public static <T> List<T>  getDataByExcel(MultipartFile multipartFile, int headerNum, Class<T> cls,Object sheetIndexOrName) throws ExcelException{
        List<T> data = null;
        // 默认值
        if(headerNum == -1 ){
            headerNum = ExcelReflectionUtil.getHeadNum(cls);
        }
        if(sheetIndexOrName == null){
            sheetIndexOrName = 0;
        }
        try {
            InputStream is = multipartFile.getInputStream();
            ExcelImport excelImport = new ExcelImport(multipartFile.getOriginalFilename(),is,headerNum,sheetIndexOrName);
            data = excelImport.getDataList(cls, ExcelField.Type.IMPORT);
        } catch (IOException e) {
            e.printStackTrace();
            throw  new ExcelException("读取文件异常");
        }
        return data;
    }




    /**
     * 指定Sheet获取数据
     * @param filePath    文件路径  C:\Users\heyongjie\Desktop\testReadExcel.xlsx
     * @param headerNum   如果使用的是导出模板然后在导入请传入-1
     * @param cls         Excel注解标志类
     * @param sheet       指定的sheet
     * @param <T>         泛型
     * @return            读取Excel后产生的数据
     */
    public static <T> List<T>  getDataByExcelAndSheet(String filePath, int headerNum, Class<T> cls, ExcelField.Type type,Object sheet){
        List<T> data = Lists.newArrayList();
        try {
            if(headerNum == -1 ){
                headerNum = ExcelReflectionUtil.getHeadNum(cls);
            }
            if(sheet == null){
                sheet = 0;
            }
            ExcelImport  excelImport = new ExcelImport(new File(filePath),headerNum,sheet);
            data = excelImport.getDataList(cls,type);
        } catch (Exception e) {
            e.printStackTrace();
            // 文件未找到异常
        }
        return data;
    }




}
