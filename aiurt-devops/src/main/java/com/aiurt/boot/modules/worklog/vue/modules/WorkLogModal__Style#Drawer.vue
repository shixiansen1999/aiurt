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
          label="巡检编号">
          <a-input placeholder="请输入巡检编号" v-decorator="['patrolCode', validatorRules.patrolCode ]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="检修编号">
          <a-input placeholder="请输入检修编号" v-decorator="['repairCode', validatorRules.repairCode ]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="故障编号">
          <a-input placeholder="请输入故障编号" v-decorator="['faultCode', validatorRules.faultCode ]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="保存状态">
          <a-input-number v-decorator="[ 'status', validatorRules.status ]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="保存状态:0.保存 1.提交 2.确认 3.审阅">
          <a-input placeholder="请输入保存状态:0.保存 1.提交 2.确认 3.审阅" v-decorator="['submitId', validatorRules.submitId ]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="提交时间">
          <a-date-picker showTime format='YYYY-MM-DD HH:mm:ss' v-decorator="[ 'submitTime', validatorRules.submitTime ]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="工作内容">
          <a-input placeholder="请输入工作内容" v-decorator="['workContent', validatorRules.workContent ]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="交接班内容">
          <a-input placeholder="请输入交接班内容" v-decorator="['content', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="附件链接">
          <a-input placeholder="请输入附件链接" v-decorator="['url', validatorRules.url ]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="接班人id">
          <a-input placeholder="请输入接班人id" v-decorator="['succeedId', validatorRules.succeedId ]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="接班人确认时间">
          <a-date-picker showTime format='YYYY-MM-DD HH:mm:ss' v-decorator="[ 'succeedTime', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="审批人">
          <a-input placeholder="请输入审批人" v-decorator="['approverId', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="审批时间">
          <a-date-picker showTime format='YYYY-MM-DD HH:mm:ss' v-decorator="[ 'approvalTime', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="删除状态:0.未删除 1已删除">
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
    name: "WorkLogDepotModal",
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
        patrolCode:{rules: [{ required: true, message: '请输入巡检编号!' }]},
        repairCode:{rules: [{ required: true, message: '请输入检修编号!' }]},
        faultCode:{rules: [{ required: true, message: '请输入故障编号!' }]},
        status:{rules: [{ required: true, message: '请输入保存状态!' }]},
        submitId:{rules: [{ required: true, message: '请输入保存状态:0.保存 1.提交 2.确认 3.审阅!' }]},
        submitTime:{rules: [{ required: true, message: '请输入提交时间!' }]},
        workContent:{rules: [{ required: true, message: '请输入工作内容!' }]},
        url:{rules: [{ required: true, message: '请输入附件链接!' }]},
        succeedId:{rules: [{ required: true, message: '请输入接班人id!' }]},
        delFlag:{rules: [{ required: true, message: '请输入删除状态:0.未删除 1已删除!' }]},
        },
        url: {
          add: "/worklog/workLogDepot/add",
          edit: "/worklog/workLogDepot/edit",
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
          this.form.setFieldsValue(pick(this.model,'patrolCode','repairCode','faultCode','status','submitId','workContent','content','url','succeedId','approverId','delFlag'))
		  //时间格式化
          this.form.setFieldsValue({submitTime:this.model.submitTime?moment(this.model.submitTime):null})
          this.form.setFieldsValue({succeedTime:this.model.succeedTime?moment(this.model.succeedTime):null})
          this.form.setFieldsValue({approvalTime:this.model.approvalTime?moment(this.model.approvalTime):null})
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
            formData.submitTime = formData.submitTime?formData.submitTime.format('YYYY-MM-DD HH:mm:ss'):null;
            formData.succeedTime = formData.succeedTime?formData.succeedTime.format('YYYY-MM-DD HH:mm:ss'):null;
            formData.approvalTime = formData.approvalTime?formData.approvalTime.format('YYYY-MM-DD HH:mm:ss'):null;
            
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