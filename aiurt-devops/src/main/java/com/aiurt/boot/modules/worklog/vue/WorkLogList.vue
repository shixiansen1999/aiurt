<template>
  <a-card :bordered="false">

    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">

          <a-col :md="6" :sm="8">
            <a-form-item label="巡检编号">
              <a-input placeholder="请输入巡检编号" v-model="queryParam.patrolCode"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <a-form-item label="检修编号">
              <a-input placeholder="请输入检修编号" v-model="queryParam.repairCode"></a-input>
            </a-form-item>
          </a-col>
        <template v-if="toggleSearchStatus">
        <a-col :md="6" :sm="8">
            <a-form-item label="故障编号">
              <a-input placeholder="请输入故障编号" v-model="queryParam.faultCode"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <a-form-item label="保存状态">
              <a-input placeholder="请输入保存状态" v-model="queryParam.status"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <a-form-item label="保存状态:0.保存 1.提交 2.确认 3.审阅">
              <a-input placeholder="请输入保存状态:0.保存 1.提交 2.确认 3.审阅" v-model="queryParam.submitId"></a-input>
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
      <a-button type="primary" icon="download" @click="handleExportXls('工作日志')">导出</a-button>
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
    <workLogDepot-modal ref="modalForm" @ok="modalFormOk"></workLogDepot-modal>
  </a-card>
</template>

<script>
  import WorkLogDepotModal from './modules/WorkLogModal'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'

  export default {
    name: "WorkLogDepotList",
    mixins:[JeecgListMixin],
    components: {
      WorkLogDepotModal
    },
    data () {
      return {
        description: '工作日志管理页面',
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
            title: '巡检编号',
            align:"center",
            dataIndex: 'patrolCode'
           },
		   {
            title: '检修编号',
            align:"center",
            dataIndex: 'repairCode'
           },
		   {
            title: '故障编号',
            align:"center",
            dataIndex: 'faultCode'
           },
		   {
            title: '保存状态',
            align:"center",
            dataIndex: 'status'
           },
		   {
            title: '保存状态:0.保存 1.提交 2.确认 3.审阅',
            align:"center",
            dataIndex: 'submitId'
           },
		   {
            title: '提交时间',
            align:"center",
            dataIndex: 'submitTime'
           },
		   {
            title: '工作内容',
            align:"center",
            dataIndex: 'workContent'
           },
		   {
            title: '交接班内容',
            align:"center",
            dataIndex: 'content'
           },
		   {
            title: '附件链接',
            align:"center",
            dataIndex: 'url'
           },
		   {
            title: '接班人id',
            align:"center",
            dataIndex: 'succeedId'
           },
		   {
            title: '接班人确认时间',
            align:"center",
            dataIndex: 'succeedTime'
           },
		   {
            title: '审批人',
            align:"center",
            dataIndex: 'approverId'
           },
		   {
            title: '审批时间',
            align:"center",
            dataIndex: 'approvalTime'
           },
		   {
            title: '删除状态:0.未删除 1已删除',
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
          list: "/worklog/workLogDepot/list",
          delete: "/worklog/workLogDepot/delete",
          deleteBatch: "/worklog/workLogDepot/deleteBatch",
          exportXlsUrl: "worklog/workLogDepot/exportXls",
          importExcelUrl: "worklog/workLogDepot/importExcel",
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