package com.aiurt.modules.train.task.service.impl;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Test1 {/*
    public static void main(String[] args) {

        String fileName = "C:\\Users\\fgw\\Desktop\\2021年运营事业总部内部培训计划表(3).xlsx";

        NoModelDataListener noModelDataListener1 = new NoModelDataListener();
        // 这里 只要，然后读取第一个sheet 同步读取会自动finish
        EasyExcel.read(fileName, DemoData.class,noModelDataListener1)   .extraRead(CellExtraTypeEnum.MERGE)  // 须要读取合并单元格信息 默认不读取
                //.registerConverter(new EmptyConverter()) //默认：DefaultConverterLoader#loadDefaultReadConverter()
                .ignoreEmptyRow(false) // 不能忽略行
                .autoTrim(true).sheet().doRead();
        BdTrainPlan bdTrainPlan = noModelDataListener1.getBdTrainPlan();
        List<BdTrainPlanSub> list = noModelDataListener1.getList();
        System.out.println("123");

    }

    @PostMapping("/excel")
    public BdTrainPlan imoprtDeviceInfo(HttpServletRequest request, HttpServletResponse response) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile file = multipartRequest.getFile("file");// 获取上传excel文件

        // 判断是否是以。xls 或者.xlsx 结尾

        String type = FilenameUtils.getExtension(file.getOriginalFilename());

        if (!StrUtil.equalsAny(type, true, "xls", "xlsx")) {
            throw new JeecgBootException("格式错误，仅支持excel文件");
        }
        NoModelDataListener noModelDataListener1 = new NoModelDataListener();
        try (InputStream inputStream = file.getInputStream()) {
            EasyExcel.read(inputStream, DemoData.class,noModelDataListener1).extraRead(CellExtraTypeEnum.MERGE)
                    .ignoreEmptyRow(false)
                    .autoTrim(true).sheet().doRead();

        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        BdTrainPlan bdTrainPlan = noModelDataListener1.getBdTrainPlan();
        List<BdTrainPlanSub> list = noModelDataListener1.getList();
        bdTrainPlan.setSubList(list);
        return bdTrainPlan;
    }*/
}
