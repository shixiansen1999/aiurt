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
            <a-form-item label="维修记录id">
              <a-input placeholder="请输入维修记录id" v-model="queryParam.repairRecordId"></a-input>
            </a-form-item>
          </a-col>
        <template v-if="toggleSearchStatus">
        <a-col :md="6" :sm="8">
            <a-form-item label="原备件编号">
              <a-input placeholder="请输入原备件编号" v-model="queryParam.oldSparePartCode"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <a-form-item label="原备件数量">
              <a-input placeholder="请输入原备件数量" v-model="queryParam.oldSparePartNum"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <a-form-item label="新备件编号">
              <a-input placeholder="请输入新备件编号" v-model="queryParam.newSparePartCode"></a-input>
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
      <a-button type="primary" icon="download" @click="handleExportXls('故障更换备件表')">导出</a-button>
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
    <faultChangeSparePart-modal ref="modalForm" @ok="modalFormOk"></faultChangeSparePart-modal>
  </a-card>
</template>

<script>
  import FaultChangeSparePartModal from './modules/FaultChangeSparePartModal'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'

  export default {
    name: "FaultChangeSparePartList",
    mixins:[JeecgListMixin],
    components: {
      FaultChangeSparePartModal
    },
    data () {
      return {
        description: '故障更换备件表管理页面',
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
            title: '维修记录id',
            align:"center",
            dataIndex: 'repairRecordId'
           },
		   {
            title: '原备件编号',
            align:"center",
            dataIndex: 'oldSparePartCode'
           },
		   {
            title: '原备件数量',
            align:"center",
            dataIndex: 'oldSparePartNum'
           },
		   {
            title: '新备件编号',
            align:"center",
            dataIndex: 'newSparePartCode'
           },
		   {
            title: '新备件数量',
            align:"center",
            dataIndex: 'newSparePartNum'
           },
          {
            title: '操作',
            dataIndex: 'action',
            align:"center",
            scopedSlots: { customRender: 'action' },
          }
        ],
		url: {
          list: "/fault/faultChangeSparePart/list",
          delete: "/fault/faultChangeSparePart/delete",
          deleteBatch: "/fault/faultChangeSparePart/deleteBatch",
          exportXlsUrl: "fault/faultChangeSparePart/exportXls",
          importExcelUrl: "fault/faultChangeSparePart/importExcel",
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