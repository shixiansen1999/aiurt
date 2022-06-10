<template>
  <a-card :bordered="false">

    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">

          <a-col :md="6" :sm="8">
            <a-form-item label="盘点任务单号">
              <a-input placeholder="请输入盘点任务单号" v-model="queryParam.stockCheckCode"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <a-form-item label="物资单号">
              <a-input placeholder="请输入物资单号" v-model="queryParam.materialCode"></a-input>
            </a-form-item>
          </a-col>
        <template v-if="toggleSearchStatus">
        <a-col :md="6" :sm="8">
            <a-form-item label="仓库编号">
              <a-input placeholder="请输入仓库编号" v-model="queryParam.warehouseCode"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <a-form-item label="实盘数量">
              <a-input placeholder="请输入实盘数量" v-model="queryParam.actualNum"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <a-form-item label="盘盈数量">
              <a-input placeholder="请输入盘盈数量" v-model="queryParam.profitNum"></a-input>
            </a-form-item>
          </a-col>
          </template>
          <a-col :md="6" :sm="8" >
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
              <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
              <a @click="handleToggleSearch" style="margin-left: 8px">
                {{ toggleSearchStatus ? '收起' : '展开' }}
                <a-icon :type="toggleSearchStatus ? 'up' : 'down'"/>
              </a>
            </span>
          </a-col>

        </a-row>
      </a-form>
    </div>

    <!-- 操作按钮区域 -->
    <div class="table-operator">
      <a-button @click="handleAdd" type="primary" icon="plus">新增</a-button>
      <a-button type="primary" icon="download" @click="handleExportXls('二级库盘点列表记录')">导出</a-button>
      <a-upload name="file" :showUploadList="false" :multiple="false" :headers="tokenHeader" :action="importExcelUrl" @change="handleImportExcel">
        <a-button type="primary" icon="import">导入</a-button>
      </a-upload>
      <a-dropdown v-if="selectedRowKeys.length > 0">
        <a-menu slot="overlay">
          <a-menu-item key="1" @click="batchDel"><a-icon type="delete"/>删除</a-menu-item>
        </a-menu>
        <a-button style="margin-left: 8px"> 批量操作 <a-icon type="down" /></a-button>
      </a-dropdown>
    </div>

    <!-- table区域-begin -->
    <div>
      <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">
        <i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择 <a style="font-weight: 600">{{ selectedRowKeys.length }}</a>项
        <a style="margin-left: 24px" @click="onClearSelected">清空</a>
      </div>

      <a-table
        ref="table"
        size="middle"
        bordered
        rowKey="id"
        :columns="columns"
        :dataSource="dataSource"
        :pagination="ipagination"
        :loading="loading"
        :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
        @change="handleTableChange">

        <span slot="action" slot-scope="text, record">
          <a @click="handleEdit(record)">编辑</a>

          <a-divider type="vertical" />
          <a-dropdown>
            <a class="ant-dropdown-link">更多 <a-icon type="down" /></a>
            <a-menu slot="overlay">
              <a-menu-item>
                <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
                  <a>删除</a>
                </a-popconfirm>
              </a-menu-item>
            </a-menu>
          </a-dropdown>
        </span>

      </a-table>
    </div>
    <!-- table区域-end -->

    <!-- 表单区域 -->
    <stockLevel2CheckDetail-modal ref="modalForm" @ok="modalFormOk"></stockLevel2CheckDetail-modal>
  </a-card>
</template>

<script>
  import StockLevel2CheckDetailModal from './modules/StockLevel2CheckDetailModal'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'

  export default {
    name: "StockLevel2CheckDetailList",
    mixins:[JeecgListMixin],
    components: {
      StockLevel2CheckDetailModal
    },
    data () {
      return {
        description: '二级库盘点列表记录管理页面',
        // 表头
        columns: [
          {
            title: '#',
            dataIndex: '',
            key:'rowIndex',
            width:60,
            align:"center",
            customRender:function (t,r,index) {
              return parseInt(index)+1;
            }
           },
		   {
            title: '盘点任务单号',
            align:"center",
            dataIndex: 'stockCheckCode'
           },
		   {
            title: '物资单号',
            align:"center",
            dataIndex: 'materialCode'
           },
		   {
            title: '仓库编号',
            align:"center",
            dataIndex: 'warehouseCode'
           },
		   {
            title: '实盘数量',
            align:"center",
            dataIndex: 'actualNum'
           },
		   {
            title: '盘盈数量',
            align:"center",
            dataIndex: 'profitNum'
           },
		   {
            title: '盘亏数量',
            align:"center",
            dataIndex: 'lossNum'
           },
		   {
            title: '备注',
            align:"center",
            dataIndex: 'note'
           },
		   {
            title: '删除状态(0.未删除 1.已删除)',
            align:"center",
            dataIndex: 'delFlag'
           },
          {
            title: '操作',
            dataIndex: 'action',
            align:"center",
            scopedSlots: { customRender: 'action' },
          }
        ],
		url: {
          list: "/secondLevelWarehouse/stockLevel2CheckDetail/list",
          delete: "/secondLevelWarehouse/stockLevel2CheckDetail/delete",
          deleteBatch: "/secondLevelWarehouse/stockLevel2CheckDetail/deleteBatch",
          exportXlsUrl: "secondLevelWarehouse/stockLevel2CheckDetail/exportXls",
          importExcelUrl: "secondLevelWarehouse/stockLevel2CheckDetail/importExcel",
       },
    }
  },
  computed: {
    importExcelUrl: function(){
      return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
    }
  },
    methods: {
     
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less'
</style>