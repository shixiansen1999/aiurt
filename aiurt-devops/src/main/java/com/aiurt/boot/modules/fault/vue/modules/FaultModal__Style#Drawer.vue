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
          label="故障编号，示例：G101.2109.001">
          <a-input placeholder="请输入故障编号，示例：G101.2109.001" v-decorator="['code', validatorRules.code ]" />
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
          label="故障设备编号集合">
          <a-input placeholder="请输入故障设备编号集合" v-decorator="['devicesIds', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="故障现象">
          <a-input placeholder="请输入故障现象" v-decorator="['faultPhenomenon', validatorRules.faultPhenomenon ]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="故障类型">
          <a-input-number v-decorator="[ 'faultType', validatorRules.faultType ]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="状态：0-新报修 1-维修中 2-维修完成">
          <a-input-number v-decorator="[ 'status', validatorRules.status ]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="创建人id">
          <a-input placeholder="请输入创建人id" v-decorator="['createrId', validatorRules.createrId ]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="系统编号">
          <a-input placeholder="请输入系统编号" v-decorator="['systemCode', validatorRules.systemCode ]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="删除状态：0.未删除 1已删除">
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
    name: "FaultModal",
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
        code:{rules: [{ required: true, message: '请输入故障编号，示例：G101.2109.001!' }]},
        lineCode:{rules: [{ required: true, message: '请输入线路编号!' }]},
        stationCode:{rules: [{ required: true, message: '请输入站点编号!' }]},
        faultPhenomenon:{rules: [{ required: true, message: '请输入故障现象!' }]},
        faultType:{rules: [{ required: true, message: '请输入故障类型!' }]},
        status:{rules: [{ required: true, message: '请输入状态：0-新报修 1-维修中 2-维修完成!' }]},
        createrId:{rules: [{ required: true, message: '请输入创建人id!' }]},
        systemCode:{rules: [{ required: true, message: '请输入系统编号!' }]},
        delFlag:{rules: [{ required: true, message: '请输入删除状态：0.未删除 1已删除!' }]},
        },
        url: {
          add: "/fault/fault/add",
          edit: "/fault/fault/edit",
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
          this.form.setFieldsValue(pick(this.model,'code','lineCode','stationCode','devicesIds','faultPhenomenon','faultType','status','createrId','systemCode','delFlag'))
		  //时间格式化
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