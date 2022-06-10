<template>
  <a-card :bordered="false">

    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">

          <a-col :md="6" :sm="8">
            <a-form-item label="申领编号">
              <a-input placeholder="请输入申领编号" v-model="queryParam.code"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <a-form-item label="申领仓库 备件库">
              <a-input placeholder="请输入申领仓库 备件库" v-model="queryParam.warehouseCode"></a-input>
            </a-form-item>
          </a-col>
        <template v-if="toggleSearchStatus">
        <a-col :md="6" :sm="8">
            <a-form-item label="出库仓库 二级库">
              <a-input placeholder="请输入出库仓库 二级库" v-model="queryParam.outWarehouseCode"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <a-form-item label="提交状态（0-未提交 1-已提交）">
              <a-input placeholder="请输入提交状态（0-未提交 1-已提交）" v-model="queryParam.commitStatus"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <a-form-item label="状态（0-未审核 1-已审核）">
              <a-input placeholder="请输入状态（0-未审核 1-已审核）" v-model="queryParam.status"></a-input>
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
      <a-button type="primary" icon="download" @click="handleExportXls('备件申领')">导出</a-button>
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
    <sparePartApply-modal ref="modalForm" @ok="modalFormOk"></sparePartApply-modal>
  </a-card>
</template>

<script>
  import SparePartApplyModal from './modules/SparePartApplyModal'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'

  export default {
    name: "SparePartApplyList",
    mixins:[JeecgListMixin],
    components: {
      SparePartApplyModal
    },
    data () {
      return {
        description: '备件申领管理页面',
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
            title: '申领编号',
            align:"center",
            dataIndex: 'code'
           },
		   {
            title: '申领仓库 备件库',
            align:"center",
            dataIndex: 'warehouseCode'
           },
		   {
            title: '出库仓库 二级库',
            align:"center",
            dataIndex: 'outWarehouseCode'
           },
		   {
            title: '提交状态（0-未提交 1-已提交）',
            align:"center",
            dataIndex: 'commitStatus'
           },
		   {
            title: '状态（0-未审核 1-已审核）',
            align:"center",
            dataIndex: 'status'
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
          list: "/secondLevelWarehouse/sparePartApply/list",
          delete: "/secondLevelWarehouse/sparePartApply/delete",
          deleteBatch: "/secondLevelWarehouse/sparePartApply/deleteBatch",
          exportXlsUrl: "secondLevelWarehouse/sparePartApply/exportXls",
          importExcelUrl: "secondLevelWarehouse/sparePartApply/importExcel",
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