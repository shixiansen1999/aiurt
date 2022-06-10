<template>
  <a-card :bordered="false">

    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">

          <a-col :md="6" :sm="8">
            <a-form-item label="设备名称">
              <a-input placeholder="请输入设备名称" v-model="queryParam.name"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <a-form-item label="设备编号">
              <a-input placeholder="请输入设备编号" v-model="queryParam.code"></a-input>
            </a-form-item>
          </a-col>
        <template v-if="toggleSearchStatus">
        <a-col :md="6" :sm="8">
            <a-form-item label="分类编号">
              <a-input placeholder="请输入分类编号" v-model="queryParam.typeCode"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <a-form-item label="子系统编号">
              <a-input placeholder="请输入子系统编号" v-model="queryParam.systemCode"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <a-form-item label="规格">
              <a-input placeholder="请输入规格" v-model="queryParam.specifications"></a-input>
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
      <a-button type="primary" icon="download" @click="handleExportXls('设备')">导出</a-button>
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
    <device-modal ref="modalForm" @ok="modalFormOk"></device-modal>
  </a-card>
</template>

<script>
  import DeviceModal from './modules/DeviceModal'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'

  export default {
    name: "DeviceList",
    mixins:[JeecgListMixin],
    components: {
      DeviceModal
    },
    data () {
      return {
        description: '设备管理页面',
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
            title: '设备名称',
            align:"center",
            dataIndex: 'name'
           },
		   {
            title: '设备编号',
            align:"center",
            dataIndex: 'code'
           },
		   {
            title: '分类编号',
            align:"center",
            dataIndex: 'typeCode'
           },
		   {
            title: '子系统编号',
            align:"center",
            dataIndex: 'systemCode'
           },
		   {
            title: '规格',
            align:"center",
            dataIndex: 'specifications'
           },
		   {
            title: '线路编号',
            align:"center",
            dataIndex: 'lineCode'
           },
		   {
            title: '站点编号',
            align:"center",
            dataIndex: 'stationCode'
           },
		   {
            title: '位置',
            align:"center",
            dataIndex: 'location'
           },
		   {
            title: '资产编号',
            align:"center",
            dataIndex: 'assetCode'
           },
		   {
            title: '品牌',
            align:"center",
            dataIndex: 'brand'
           },
		   {
            title: '出厂编号',
            align:"center",
            dataIndex: 'factoryCode'
           },
		   {
            title: '生产商',
            align:"center",
            dataIndex: 'manufacturer'
           },
		   {
            title: '供货厂商',
            align:"center",
            dataIndex: 'supplier'
           },
		   {
            title: '生产日期',
            align:"center",
            dataIndex: 'productionDate'
           },
		   {
            title: '开始使用日期',
            align:"center",
            dataIndex: 'startDate'
           },
		   {
            title: '使用年限',
            align:"center",
            dataIndex: 'serviceLife'
           },
		   {
            title: '技术参数',
            align:"center",
            dataIndex: 'technicalParameter'
           },
		   {
            title: '状态 0-停用 1-正常',
            align:"center",
            dataIndex: 'status'
           },
		   {
            title: '删除状态 0-未删除 1-已删除',
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
          list: "/device/device/list",
          delete: "/device/device/delete",
          deleteBatch: "/device/device/deleteBatch",
          exportXlsUrl: "device/device/exportXls",
          importExcelUrl: "device/device/importExcel",
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