<template>
  <a-card :bordered="false">

    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">

          <a-col :md="6" :sm="8">
            <a-form-item label="故障编号，示例：G101.2109.001">
              <a-input placeholder="请输入故障编号，示例：G101.2109.001" v-model="queryParam.faultCode"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <a-form-item label="指派人">
              <a-input placeholder="请输入指派人" v-model="queryParam.appointUserId"></a-input>
            </a-form-item>
          </a-col>
        <template v-if="toggleSearchStatus">
        <a-col :md="6" :sm="8">
            <a-form-item label="作业类型">
              <a-input placeholder="请输入作业类型" v-model="queryParam.workType"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <a-form-item label="计划令编码">
              <a-input placeholder="请输入计划令编码" v-model="queryParam.planOrderCode"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <a-form-item label="计划令图片">
              <a-input placeholder="请输入计划令图片" v-model="queryParam.planOrderImg"></a-input>
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
      <a-button type="primary" icon="download" @click="handleExportXls('故障维修记录表')">导出</a-button>
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
    <faultRepairRecord-modal ref="modalForm" @ok="modalFormOk"></faultRepairRecord-modal>
  </a-card>
</template>

<script>
  import FaultRepairRecordModal from './modules/FaultRepairRecordModal'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'

  export default {
    name: "FaultRepairRecordList",
    mixins:[JeecgListMixin],
    components: {
      FaultRepairRecordModal
    },
    data () {
      return {
        description: '故障维修记录表管理页面',
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
            title: '故障编号，示例：G101.2109.001',
            align:"center",
            dataIndex: 'faultCode'
           },
		   {
            title: '指派人',
            align:"center",
            dataIndex: 'appointUserId'
           },
		   {
            title: '作业类型',
            align:"center",
            dataIndex: 'workType'
           },
		   {
            title: '计划令编码',
            align:"center",
            dataIndex: 'planOrderCode'
           },
		   {
            title: '计划令图片',
            align:"center",
            dataIndex: 'planOrderImg'
           },
		   {
            title: '参与人id集合',
            align:"center",
            dataIndex: 'participateIds'
           },
		   {
            title: '故障现象',
            align:"center",
            dataIndex: 'faultPhenomenon'
           },
		   {
            title: '故障分析',
            align:"center",
            dataIndex: 'faultAnalysis'
           },
		   {
            title: '维修措施',
            align:"center",
            dataIndex: 'maintenanceMeasures'
           },
		   {
            title: '问题解决状态：0-未解决 1-已解决',
            align:"center",
            dataIndex: 'solveStatus'
           },
		   {
            title: '状态：0-未提交 1-已提交',
            align:"center",
            dataIndex: 'status'
           },
		   {
            title: '创建人id',
            align:"center",
            dataIndex: 'createrId'
           },
		   {
            title: '删除状态：0.未删除 1已删除',
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
          list: "/fault/faultRepairRecord/list",
          delete: "/fault/faultRepairRecord/delete",
          deleteBatch: "/fault/faultRepairRecord/deleteBatch",
          exportXlsUrl: "fault/faultRepairRecord/exportXls",
          importExcelUrl: "fault/faultRepairRecord/importExcel",
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