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
          label="计划名称">
          <a-input placeholder="请输入计划名称" v-decorator="['name', validatorRules.name ]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="主讲人">
          <a-input placeholder="请输入主讲人" v-decorator="['presenter', validatorRules.presenter ]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="培训方式 数据字典配置">
          <a-input-number v-decorator="[ 'trainingMethods', validatorRules.trainingMethods ]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="培训类型 数据字典配置">
          <a-input-number v-decorator="[ 'trainingType', validatorRules.trainingType ]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="培训地点">
          <a-input placeholder="请输入培训地点" v-decorator="['address', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="开始日期">
          <a-date-picker v-decorator="[ 'startDate', validatorRules.startDate ]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="结束日期">
          <a-date-picker v-decorator="[ 'endDate', validatorRules.endDate ]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="课时（分钟）">
          <a-input-number v-decorator="[ 'classHour', validatorRules.classHour ]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="课件id集合">
          <a-input placeholder="请输入课件id集合" v-decorator="['coursewares', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="说明">
          <a-input placeholder="请输入说明" v-decorator="['remarks', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="删除状态 0-未删除 1-已删除">
          <a-input-number v-decorator="[ 'delFlag', validatorRules.delFlag ]" />
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
    name: "TrainingPlanModal",
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
        name:{rules: [{ required: true, message: '请输入计划名称!' }]},
        presenter:{rules: [{ required: true, message: '请输入主讲人!' }]},
        trainingMethods:{rules: [{ required: true, message: '请输入培训方式 数据字典配置!' }]},
        trainingType:{rules: [{ required: true, message: '请输入培训类型 数据字典配置!' }]},
        startDate:{rules: [{ required: true, message: '请输入开始日期!' }]},
        endDate:{rules: [{ required: true, message: '请输入结束日期!' }]},
        classHour:{rules: [{ required: true, message: '请输入课时（分钟）!' }]},
        delFlag:{rules: [{ required: true, message: '请输入删除状态 0-未删除 1-已删除!' }]},
        },
        url: {
          add: "/training/trainingPlan/add",
          edit: "/training/trainingPlan/edit",
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
          this.form.setFieldsValue(pick(this.model,'name','presenter','trainingMethods','trainingType','address','classHour','coursewares','remarks','delFlag'))
		  //时间格式化
          this.form.setFieldsValue({startDate:this.model.startDate?moment(this.model.startDate):null})
          this.form.setFieldsValue({endDate:this.model.endDate?moment(this.model.endDate):null})
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
            formData.endDate = formData.endDate?formData.endDate.format():null;
            
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