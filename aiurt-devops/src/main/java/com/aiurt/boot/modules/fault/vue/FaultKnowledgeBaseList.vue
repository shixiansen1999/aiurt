<template>
  <a-card :bordered="false">

    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">

          <a-col :md="6" :sm="8">
            <a-form-item label="类型id">
              <a-input placeholder="请输入类型id" v-model="queryParam.typeId"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <a-form-item label="故障类型">
              <a-input placeholder="请输入故障类型" v-model="queryParam.faultType"></a-input>
            </a-form-item>
          </a-col>
        <template v-if="toggleSearchStatus">
        <a-col :md="6" :sm="8">
            <a-form-item label="故障现象">
              <a-input placeholder="请输入故障现象" v-model="queryParam.faultPhenomenon"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <a-form-item label="故障原因">
              <a-input placeholder="请输入故障原因" v-model="queryParam.faultReason"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <a-form-item label="故障措施/解决方案">
              <a-input placeholder="请输入故障措施/解决方案" v-model="queryParam.solution"></a-input>
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
      <a-button type="primary" icon="download" @click="handleExportXls('故障知识库')">导出</a-button>
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
    <faultKnowledgeBase-modal ref="modalForm" @ok="modalFormOk"></faultKnowledgeBase-modal>
  </a-card>
</template>

<script>
  import FaultKnowledgeBaseModal from './modules/FaultKnowledgeBaseModal'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'

  export default {
    name: "FaultKnowledgeBaseList",
    mixins:[JeecgListMixin],
    components: {
      FaultKnowledgeBaseModal
    },
    data () {
      return {
        description: '故障知识库管理页面',
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
            title: '类型id',
            align:"center",
            dataIndex: 'typeId'
           },
		   {
            title: '故障类型',
            align:"center",
            dataIndex: 'faultType'
           },
		   {
            title: '故障现象',
            align:"center",
            dataIndex: 'faultPhenomenon'
           },
		   {
            title: '故障原因',
            align:"center",
            dataIndex: 'faultReason'
           },
		   {
            title: '故障措施/解决方案',
            align:"center",
            dataIndex: 'solution'
           },
		   {
            title: '关联故障集合,例:G101.2109.001，G101.2109.002',
            align:"center",
            dataIndex: 'faultCodes'
           },
		   {
            title: '删除状态:0.未删除 1已删除',
            align:"center",
            dataIndex: 'delFlag'
           },
		   {
            title: '创建人id',
            align:"center",
            dataIndex: 'createrId'
           },
          {
            title: '操作',
            dataIndex: 'action',
            align:"center",
            scopedSlots: { customRender: 'action' },
          }
        ],
		url: {
          list: "/fault/faultKnowledgeBase/list",
          delete: "/fault/faultKnowledgeBase/delete",
          deleteBatch: "/fault/faultKnowledgeBase/deleteBatch",
          exportXlsUrl: "fault/faultKnowledgeBase/exportXls",
          importExcelUrl: "fault/faultKnowledgeBase/importExcel",
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