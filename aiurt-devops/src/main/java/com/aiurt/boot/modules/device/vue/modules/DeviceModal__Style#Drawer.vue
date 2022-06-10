<template>
  <a-drawer
      :title="title"
      :width="800"
      placement="right"
      :closable="false"
      @close="close"
      :visible="visible"
  >

    <a-spin :spinning="confirmLoading">
      <a-form :form="form">
      
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="设备名称">
          <a-input placeholder="请输入设备名称" v-decorator="['name', validatorRules.name ]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="设备编号">
          <a-input placeholder="请输入设备编号" v-decorator="['code', validatorRules.code ]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="分类编号">
          <a-input placeholder="请输入分类编号" v-decorator="['typeCode', validatorRules.typeCode ]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="子系统编号">
          <a-input placeholder="请输入子系统编号" v-decorator="['systemCode', validatorRules.systemCode ]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="规格">
          <a-input placeholder="请输入规格" v-decorator="['specifications', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="线路编号">
          <a-input placeholder="请输入线路编号" v-decorator="['lineCode', validatorRules.lineCode ]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="站点编号">
          <a-input placeholder="请输入站点编号" v-decorator="['stationCode', validatorRules.stationCode ]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="位置">
          <a-input placeholder="请输入位置" v-decorator="['location', validatorRules.location ]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="资产编号">
          <a-input placeholder="请输入资产编号" v-decorator="['assetCode', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="品牌">
          <a-input placeholder="请输入品牌" v-decorator="['brand', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="出厂编号">
          <a-input placeholder="请输入出厂编号" v-decorator="['factoryCode', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="生产商">
          <a-input placeholder="请输入生产商" v-decorator="['manufacturer', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="供货厂商">
          <a-input placeholder="请输入供货厂商" v-decorator="['supplier', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="生产日期">
          <a-input placeholder="请输入生产日期" v-decorator="['productionDate', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="开始使用日期">
          <a-date-picker v-decorator="[ 'startDate', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="使用年限">
          <a-date-picker v-decorator="[ 'serviceLife', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="技术参数">
          <a-input placeholder="请输入技术参数" v-decorator="['technicalParameter', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="状态 0-停用 1-正常">
          <a-input-number v-decorator="[ 'status', validatorRules.status ]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="删除状态 0-未删除 1-已删除">
          <a-input-number v-decorator="[ 'delFlag', validatorRules.delFlag ]" />
        </a-form-item>
		
      </a-form>
    </a-spin>
    <a-button type="primary" @click="handleOk">确定</a-button>
    <a-button type="primary" @click="handleCancel">取消</a-button>
  </a-drawer>
</template>

<script>
  import { httpAction } from '@/api/manage'
  import pick from 'lodash.pick'
  import moment from "moment"

  export default {
    name: "DeviceModal",
    data () {
      return {
        title:"操作",
        visible: false,
        model: {},
        labelCol: {
          xs: { span: 24 },
          sm: { span: 5 },
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 },
        },

        confirmLoading: false,
        form: this.$form.createForm(this),
        validatorRules:{
        name:{rules: [{ required: true, message: '请输入设备名称!' }]},
        code:{rules: [{ required: true, message: '请输入设备编号!' }]},
        typeCode:{rules: [{ required: true, message: '请输入分类编号!' }]},
        systemCode:{rules: [{ required: true, message: '请输入子系统编号!' }]},
        lineCode:{rules: [{ required: true, message: '请输入线路编号!' }]},
        stationCode:{rules: [{ required: true, message: '请输入站点编号!' }]},
        location:{rules: [{ required: true, message: '请输入位置!' }]},
        status:{rules: [{ required: true, message: '请输入状态 0-停用 1-正常!' }]},
        delFlag:{rules: [{ required: true, message: '请输入删除状态 0-未删除 1-已删除!' }]},
        },
        url: {
          add: "/device/device/add",
          edit: "/device/device/edit",
        },
      }
    },
    created () {
    },
    methods: {
      add () {
        this.edit({});
      },
      edit (record) {
        this.form.resetFields();
        this.model = Object.assign({}, record);
        this.visible = true;
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(this.model,'name','code','typeCode','systemCode','specifications','lineCode','stationCode','location','assetCode','brand','factoryCode','manufacturer','supplier','productionDate','technicalParameter','status','delFlag'))
		  //时间格式化
          this.form.setFieldsValue({startDate:this.model.startDate?moment(this.model.startDate):null})
          this.form.setFieldsValue({serviceLife:this.model.serviceLife?moment(this.model.serviceLife):null})
        });

      },
      close () {
        this.$emit('close');
        this.visible = false;
      },
      handleOk () {
        const that = this;
        // 触发表单验证
        this.form.validateFields((err, values) => {
          if (!err) {
            that.confirmLoading = true;
            let httpurl = '';
            let method = '';
            if(!this.model.id){
              httpurl+=this.url.add;
              method = 'post';
            }else{
              httpurl+=this.url.edit;
               method = 'put';
            }
            let formData = Object.assign(this.model, values);
            //时间格式化
            formData.startDate = formData.startDate?formData.startDate.format():null;
            formData.serviceLife = formData.serviceLife?formData.serviceLife.format():null;
            
            console.log(formData)
            httpAction(httpurl,formData,method).then((res)=>{
              if(res.success){
                that.$message.success(res.message);
                that.$emit('ok');
              }else{
                that.$message.warning(res.message);
              }
            }).finally(() => {
              that.confirmLoading = false;
              that.close();
            })



          }
        })
      },
      handleCancel () {
        this.close()
      },


    }
  }
</script>

<style lang="less" scoped>
/** Button按钮间距 */
  .ant-btn {
    margin-left: 30px;
    margin-bottom: 30px;
    float: right;
  }
</style>