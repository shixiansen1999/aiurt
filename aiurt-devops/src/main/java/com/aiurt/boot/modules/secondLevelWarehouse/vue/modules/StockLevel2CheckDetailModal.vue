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
          label="盘点任务单号">
          <a-input placeholder="请输入盘点任务单号" v-decorator="['stockCheckCode', validatorRules.stockCheckCode ]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="物资单号">
          <a-input placeholder="请输入物资单号" v-decorator="['materialCode', validatorRules.materialCode ]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="仓库编号">
          <a-input placeholder="请输入仓库编号" v-decorator="['warehouseCode', validatorRules.warehouseCode ]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="实盘数量">
          <a-input-number v-decorator="[ 'actualNum', validatorRules.actualNum ]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="盘盈数量">
          <a-input-number v-decorator="[ 'profitNum', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="盘亏数量">
          <a-input-number v-decorator="[ 'lossNum', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="备注">
          <a-input placeholder="请输入备注" v-decorator="['note', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="删除状态(0.未删除 1.已删除)">
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
    name: "StockLevel2CheckDetailModal",
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
        stockCheckCode:{rules: [{ required: true, message: '请输入盘点任务单号!' }]},
        materialCode:{rules: [{ required: true, message: '请输入物资单号!' }]},
        warehouseCode:{rules: [{ required: true, message: '请输入仓库编号!' }]},
        actualNum:{rules: [{ required: true, message: '请输入实盘数量!' }]},
        delFlag:{rules: [{ required: true, message: '请输入删除状态(0.未删除 1.已删除)!' }]},
        },
        url: {
          add: "/secondLevelWarehouse/stockLevel2CheckDetail/add",
          edit: "/secondLevelWarehouse/stockLevel2CheckDetail/edit",
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
          this.form.setFieldsValue(pick(this.model,'stockCheckCode','materialCode','warehouseCode','actualNum','profitNum','lossNum','note','delFlag'))
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