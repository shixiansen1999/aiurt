<template>
  <a-modal
    :title="title"
    :width="800"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭">
    
    <a-spin :spinning="confirmLoading">
      <a-form :form="form">
      
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="故障编号，示例：G101.2109.001">
          <a-input placeholder="请输入故障编号，示例：G101.2109.001" v-decorator="['faultCode', validatorRules.faultCode ]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="故障分析">
          <a-input placeholder="请输入故障分析" v-decorator="['faultAnalysis', validatorRules.faultAnalysis ]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="解决方案">
          <a-input placeholder="请输入解决方案" v-decorator="['solution', validatorRules.solution ]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="删除状态：0.未删除 1已删除">
          <a-input-number v-decorator="[ 'delFlag', validatorRules.delFlag ]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="创建人id">
          <a-input placeholder="请输入创建人id" v-decorator="['createrId', validatorRules.createrId ]" />
        </a-form-item>
		
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>
  import { httpAction } from '@/api/manage'
  import pick from 'lodash.pick'
  import moment from "moment"

  export default {
    name: "FaultAnalysisReportModal",
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
        faultCode:{rules: [{ required: true, message: '请输入故障编号，示例：G101.2109.001!' }]},
        faultAnalysis:{rules: [{ required: true, message: '请输入故障分析!' }]},
        solution:{rules: [{ required: true, message: '请输入解决方案!' }]},
        delFlag:{rules: [{ required: true, message: '请输入删除状态：0.未删除 1已删除!' }]},
        createrId:{rules: [{ required: true, message: '请输入创建人id!' }]},
        },
        url: {
          add: "/fault/faultAnalysisReport/add",
          edit: "/fault/faultAnalysisReport/edit",
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
          this.form.setFieldsValue(pick(this.model,'faultCode','faultAnalysis','solution','delFlag','createrId'))
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

</style>