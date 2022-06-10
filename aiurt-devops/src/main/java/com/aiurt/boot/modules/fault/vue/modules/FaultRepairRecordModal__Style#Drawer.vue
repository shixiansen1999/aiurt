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
          <a-input placeholder="请输入故障编号，示例：G101.2109.001" v-decorator="['faultCode', validatorRules.faultCode ]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="指派人">
          <a-input-number v-decorator="[ 'appointUserId', validatorRules.appointUserId ]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="作业类型">
          <a-input placeholder="请输入作业类型" v-decorator="['workType', validatorRules.workType ]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="计划令编码">
          <a-input placeholder="请输入计划令编码" v-decorator="['planOrderCode', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="计划令图片">
          <a-input placeholder="请输入计划令图片" v-decorator="['planOrderImg', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="参与人id集合">
          <a-input placeholder="请输入参与人id集合" v-decorator="['participateIds', {}]" />
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
          label="故障分析">
          <a-input placeholder="请输入故障分析" v-decorator="['faultAnalysis', validatorRules.faultAnalysis ]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="维修措施">
          <a-input placeholder="请输入维修措施" v-decorator="['maintenanceMeasures', validatorRules.maintenanceMeasures ]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="问题解决状态：0-未解决 1-已解决">
          <a-input-number v-decorator="[ 'solveStatus', validatorRules.solveStatus ]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="状态：0-未提交 1-已提交">
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
    name: "FaultRepairRecordModal",
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
        appointUserId:{rules: [{ required: true, message: '请输入指派人!' }]},
        workType:{rules: [{ required: true, message: '请输入作业类型!' }]},
        faultPhenomenon:{rules: [{ required: true, message: '请输入故障现象!' }]},
        faultAnalysis:{rules: [{ required: true, message: '请输入故障分析!' }]},
        maintenanceMeasures:{rules: [{ required: true, message: '请输入维修措施!' }]},
        solveStatus:{rules: [{ required: true, message: '请输入问题解决状态：0-未解决 1-已解决!' }]},
        status:{rules: [{ required: true, message: '请输入状态：0-未提交 1-已提交!' }]},
        createrId:{rules: [{ required: true, message: '请输入创建人id!' }]},
        delFlag:{rules: [{ required: true, message: '请输入删除状态：0.未删除 1已删除!' }]},
        },
        url: {
          add: "/fault/faultRepairRecord/add",
          edit: "/fault/faultRepairRecord/edit",
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
          this.form.setFieldsValue(pick(this.model,'faultCode','appointUserId','workType','planOrderCode','planOrderImg','participateIds','faultPhenomenon','faultAnalysis','maintenanceMeasures','solveStatus','status','createrId','delFlag'))
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