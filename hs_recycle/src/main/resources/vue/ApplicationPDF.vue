<template>
  <div class="application-pdf-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <el-button icon="el-icon-arrow-left" @click="goBack">返回</el-button>
        <h2>业务申请单预览</h2>
      </div>
      <div class="header-right">
        <el-button type="primary" icon="el-icon-download" :loading="generating" @click="generatePDF">
          生成PDF
        </el-button>
        <el-button type="success" icon="el-icon-view" @click="previewPDF">
          预览PDF
        </el-button>
        <el-button type="warning" icon="el-icon-printer" @click="printPDF">
          打印
        </el-button>
        <el-button icon="el-icon-refresh" @click="refreshData">
          刷新数据
        </el-button>
      </div>
    </div>

    <!-- 申请单预览 -->
    <div ref="previewContainer" class="preview-container">
      <div class="application-preview">
        <!-- 申请单标题 -->
        <div class="application-title">
          <h1>业务申请单</h1>
        </div>

        <!-- 合同基本信息 -->
        <div class="contract-info">
          <div class="contract-parties">
            <div class="party-item">
              <span class="label">甲方：</span>
              <span class="value">{{ orderData.partyA || '回收管理有限公司' }}</span>
            </div>
            <div class="party-item">
              <span class="label">乙方：</span>
              <span class="value">{{ orderData.partyB || '合作方' + (orderData.id || '') }}</span>
            </div>
            <div class="party-item">
              <span class="label">合同编号：</span>
              <span class="value">{{ orderData.contractNo || 'CT' + (orderData.id || '') }}</span>
            </div>
          </div>
        </div>

        <!-- 申请说明 -->
        <div class="application-description">
          <p>按合同约定，我方现就该合同下编号为 <strong>{{ orderData.no || 'RO' + (orderData.id || '') }}</strong> 的订单申请执行：</p>
        </div>

        <!-- 一、订单信息 -->
        <div class="order-info-section">
          <h2 class="section-title">一、订单信息</h2>

          <!-- （1）订单类型 -->
          <div class="order-type-info">
            <p class="info-item">
              <span class="label">（1）订单类型（我方）</span>
              <span class="value">{{ getOrderTypeText(orderData.type) }}订单</span>
            </p>
          </div>

          <!-- （2）订单内容 -->
          <div class="order-content-info">
            <p class="info-item">
              <span class="label">（2）订单内容</span>
            </p>

            <!-- 订单明细表格 -->
            <div class="order-details-table">
              <table>
                <thead>
                  <tr>
                    <th>分类</th>
                    <th>名称</th>
                    <th>规格型号</th>
                    <th>备注</th>
                    <th>数量</th>
                    <th>单价</th>
                    <th>总价</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="(item, index) in orderItems" :key="index">
                    <td>{{ getItemTypeText(item.type) }}</td>
                    <td>{{ item.name }}</td>
                    <td>{{ item.specification }}</td>
                    <td>{{ item.remark || '--' }}</td>
                    <td>{{ item.quantity }}</td>
                    <td>¥{{ formatAmount(item.unitPrice) }}</td>
                    <td>¥{{ formatAmount(item.amount) }}</td>
                  </tr>
                </tbody>
              </table>
            </div>

            <!-- 批注说明 -->
            <div class="notes-section">
              <p class="notes-text">
                <strong>注：</strong>货物类数量单位为kg、运输类数量单位为km，总价单位为元。
              </p>
            </div>
          </div>

          <!-- （3）订单交付地址 -->
          <div class="delivery-address-info">
            <p class="info-item">
              <span class="label">（3）订单交付地址</span>
              <span class="value">{{ orderData.deliveryAddress || '--' }}</span>
            </p>
          </div>

          <!-- （4）交付货物送达我司地址 -->
          <div class="warehouse-address-info">
            <p class="info-item">
              <span class="label">（4）交付货物送达我司地址（交付服务计入我司成本）</span>
              <span class="value">{{ orderData.warehouseAddress || '--' }}</span>
            </p>
          </div>

          <!-- （5）订单经办人 -->
          <div class="processor-info">
            <p class="info-item">
              <span class="label">（5）订单经办人</span>
              <span class="value">您方：{{ orderData.processor || '--' }}（{{ orderData.processorPhone || '--' }}）</span>
            </p>
          </div>
        </div>

        <!-- 二、其他信息 -->
        <div class="other-info-section">
          <h2 class="section-title">二、其他信息</h2>

          <div class="other-info-content">
            <p class="info-item">
              <span class="label">订单申请时间：</span>
              <span class="value">{{ formatDateTime(orderData.startTime) }}（我方）</span>
            </p>

            <p class="info-item">
              <span class="label">订单内容流转方向：</span>
              <span class="value">{{ getFlowDirectionText(orderData.type) }}</span>
            </p>

            <p class="info-item">
              <span class="label">合同约定走款账号：</span>
              <span class="value">{{ orderData.paymentAccount || '6222021234567890123' }}</span>
            </p>
          </div>
        </div>

        <!-- 最终批注 -->
        <div class="final-notes-section">
          <p class="final-notes-text">
            <strong>注：</strong>如有疑问请于本单据发出后1个工作日内联系我司企业微信客服或经办人员 <strong>孟经理（13683335083）</strong>，过时默认同意本申请单内容。
          </p>
        </div>

        <!-- 公司信息和日期 -->
        <div class="company-info-footer">
          <div class="company-name">云南伏宁再生资源有限公司</div>
          <div class="application-date">生成申请单日期：{{ formatDate(new Date()) }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { generateSettlementPDF, getRecycleDetail } from '@/api/recycle'
import { parseTime } from '@/utils'
import {
  getOrderTypeText,
  getOrderStatusText
} from '@/constants/orderTypes'

export default {
  name: 'ApplicationPDF',
  data() {
    return {
      orderId: null,
      orderData: {},
      orderItems: [],
      generating: false
    }
  },
  created() {
    // 从路由参数获取订单ID
    this.orderId = this.$route.params.orderId || this.$route.query.orderId

    // 如果有orderId，则从API获取订单数据
    if (this.orderId) {
      this.loadOrderData()
    }
  },
  methods: {
    // 加载订单数据
    async loadOrderData() {
      if (!this.orderId) return

      try {
        const response = await getRecycleDetail(this.orderId)
        this.orderData = response.data || {}

        // 如果API返回的数据中没有items，则使用模拟数据
        if (!this.orderData.items || this.orderData.items.length === 0) {
          this.loadMockOrderItems()
        } else {
          // 将API返回的items转换为PDF需要的格式
          this.orderItems = this.orderData.items.map((item, index) => ({
            type: item.goodType || '货物', // 直接使用订单明细中的货物类型
            name: item.goodName || '商品' + (index + 1),
            specification: item.goodModel || '标准规格',
            remark: item.remark || '',
            quantity: item.goodCount || 0,
            unitPrice: item.goodPrice || 0,
            amount: item.goodTotalPrice || 0
          }))
        }
      } catch (error) {
        console.error('获取订单数据失败:', error)
        this.$message.error('获取订单数据失败')
        // 如果API调用失败，使用模拟数据
        this.loadMockOrderData()
      }
    },

    // 加载模拟订单数据（作为备用）
    loadMockOrderData() {
      this.orderData = {
        id: this.orderId,
        no: 'RO' + this.orderId,
        type: 'purchase',
        status: 'processing',
        contractNo: 'CT' + this.orderId,
        contractName: '回收合同' + this.orderId,
        partyA: '回收管理有限公司',
        partyB: '合作方' + this.orderId,
        processor: '经办人',
        processorPhone: '13800138000',
        startTime: new Date().toISOString(),
        endTime: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000).toISOString(),
        warehouseAddress: '北京市朝阳区仓库地址',
        deliveryAddress: '北京市朝阳区交付地址',
        paymentAccount: '6222021234567890123',
        totalAmount: 100000
      }
      this.loadMockOrderItems()
    },

    // 加载模拟订单明细数据
    loadMockOrderItems() {
      // 模拟订单明细数据
      this.orderItems = [
        {
          type: 'goods',
          name: '废纸',
          specification: 'A4纸',
          remark: '白色废纸',
          quantity: 1000,
          unitPrice: 2.5,
          amount: 2500
        },
        {
          type: 'goods',
          name: '废塑料',
          specification: 'PET瓶',
          remark: '透明塑料瓶',
          quantity: 500,
          unitPrice: 3.2,
          amount: 1600
        },
        {
          type: 'transport',
          name: '运输服务',
          specification: '市内运输',
          remark: '10公里内',
          quantity: 50,
          unitPrice: 15,
          amount: 750
        },
        {
          type: 'goods',
          name: '废金属',
          specification: '废铁',
          remark: '废旧钢材',
          quantity: 200,
          unitPrice: 1.8,
          amount: 360
        }
      ]
    },

    // 获取项目类型显示文本
    getItemTypeText(type) {
      // 如果type是订单明细中的货物类型（如：废纸、废塑料等），直接返回
      // 如果是系统预定义的类型，则进行映射转换
      const typeMap = {
        'goods': '货物',
        'transport': '运输',
        'service': '服务',
        'other': '其他'
      }

      // 如果在预定义映射中找到，使用映射值；否则直接返回原值（货物类型）
      return typeMap[type] || type || '未知'
    },

    // 获取流转方向文本
    getFlowDirectionText(orderType) {
      const flowMap = {
        'purchase': '采购',
        'sales': '销售',
        'process': '加工',
        'storage': '入库/出库',
        'transport': '运输',
        'other': '其他'
      }
      return flowMap[orderType] || '未知'
    },

    // 返回上一页
    goBack() {
      this.$router.go(-1)
    },

    // 生成PDF
    async generatePDF() {
      try {
        this.generating = true

        // 使用前端生成PDF
        await this.generatePDFFromFrontend()

        this.$message.success('PDF生成成功')
      } catch (error) {
        console.error('PDF生成失败:', error)
        // 如果复杂方法失败，尝试简单方法
        try {
          await this.generateSimplePDF()
          this.$message.success('PDF生成成功（简化版）')
        } catch (simpleError) {
          console.error('简化PDF生成也失败:', simpleError)
          this.$message.error('PDF生成失败，请重试')
        }
      } finally {
        this.generating = false
      }
    },

    // 使用前端生成PDF
    async generatePDFFromFrontend() {
      const previewContainer = this.$refs.previewContainer
      if (!previewContainer) {
        throw new Error('预览容器不存在')
      }

      // 动态导入PDF生成库
      const [{ default: jsPDF }, { default: html2canvas }] = await Promise.all([
        import('jspdf'),
        import('html2canvas')
      ])

      const filename = `业务申请单_${this.orderData.no || this.orderId}_${new Date().getTime()}.pdf`

      // 显示加载提示
      const loading = this.showLoading('正在生成PDF...')

      try {
        // 添加PDF模式样式
        previewContainer.classList.add('pdf-mode')

        // 等待样式应用
        await new Promise(resolve => setTimeout(resolve, 100))

        // 创建PDF文档，使用中文字体支持
        const pdf = new jsPDF({
          orientation: 'portrait',
          unit: 'mm',
          format: 'a4',
          putOnlyUsedFonts: true,
          floatPrecision: 16
        })

        // 设置页面尺寸
        const pageWidth = pdf.internal.pageSize.getWidth()
        const pageHeight = pdf.internal.pageSize.getHeight()
        const margin = 20
        const contentWidth = pageWidth - (margin * 2)
        const contentHeight = pageHeight - (margin * 2)

        // 获取元素尺寸
        const elementRect = previewContainer.getBoundingClientRect()
        const elementWidth = elementRect.width
        const elementHeight = elementRect.height

        // 转换HTML为Canvas，优化配置
        const canvas = await html2canvas(previewContainer, {
          scale: 1.5, // 适中的清晰度
          useCORS: true,
          allowTaint: false,
          backgroundColor: '#ffffff',
          width: elementWidth,
          height: elementHeight,
          logging: false,
          removeContainer: false,
          foreignObjectRendering: false,
          imageTimeout: 15000
        })

        // 计算缩放比例
        const scale = contentWidth / canvas.width
        const scaledHeight = canvas.height * scale
        const pages = Math.ceil(scaledHeight / contentHeight)

        // 添加页面
        for (let i = 0; i < pages; i++) {
          if (i > 0) {
            pdf.addPage()
          }

          // 计算当前页的裁剪区域
          const sourceY = i * (contentHeight / scale)
          const sourceHeight = Math.min(contentHeight / scale, canvas.height - sourceY)

          // 创建临时canvas来裁剪当前页内容
          const tempCanvas = document.createElement('canvas')
          const tempCtx = tempCanvas.getContext('2d')
          tempCanvas.width = canvas.width
          tempCanvas.height = sourceHeight

          // 裁剪当前页内容
          tempCtx.drawImage(
            canvas,
            0, sourceY, canvas.width, sourceHeight,
            0, 0, canvas.width, sourceHeight
          )

          // 添加页眉
          this.addPDFHeader(pdf, '业务申请单', pageWidth, margin)

          // 添加内容
          const imgData = tempCanvas.toDataURL('image/jpeg', 0.95)
          pdf.addImage(
            imgData,
            'JPEG',
            margin,
            margin + 20,
            contentWidth,
            (sourceHeight * contentWidth) / canvas.width
          )

          // 添加页脚
          this.addPDFFooter(pdf, i + 1, pages, pageWidth, pageHeight, margin)
        }

        // 隐藏加载提示
        this.hideLoading(loading)

        // 移除PDF模式样式
        previewContainer.classList.remove('pdf-mode')

        // 下载PDF
        pdf.save(filename)
      } catch (error) {
        this.hideLoading(loading)
        // 移除PDF模式样式
        previewContainer.classList.remove('pdf-mode')
        console.error('PDF生成详细错误:', error)
        throw error
      }
    },

    // 添加PDF页眉
    addPDFHeader(pdf, title, pageWidth, margin) {
      // 使用默认字体，避免中文乱码
      pdf.setFontSize(14)
      pdf.setFont('helvetica', 'bold')
      pdf.text(title, pageWidth / 2, margin + 8, { align: 'center' })

      // 添加分隔线
      pdf.setDrawColor(200, 200, 200)
      pdf.line(margin, margin + 15, pageWidth - margin, margin + 15)
    },

    // 添加PDF页脚
    addPDFFooter(pdf, currentPage, totalPages, pageWidth, pageHeight, margin) {
      const footerY = pageHeight - margin

      // 添加页码
      pdf.setFontSize(10)
      pdf.setFont('helvetica', 'normal')
      pdf.text(
        `第 ${currentPage} 页，共 ${totalPages} 页`,
        pageWidth / 2,
        footerY,
        { align: 'center' }
      )

      // 添加生成时间
      const now = new Date()
      const timeStr = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-${String(now.getDate()).padStart(2, '0')} ${String(now.getHours()).padStart(2, '0')}:${String(now.getMinutes()).padStart(2, '0')}`
      pdf.text(
        `生成时间：${timeStr}`,
        margin,
        footerY,
        { align: 'left' }
      )
    },

    // 简化版PDF生成方法
    async generateSimplePDF() {
      const previewContainer = this.$refs.previewContainer
      if (!previewContainer) {
        throw new Error('预览容器不存在')
      }

      // 动态导入PDF生成库
      const [{ default: jsPDF }, { default: html2canvas }] = await Promise.all([
        import('jspdf'),
        import('html2canvas')
      ])

      const filename = `业务申请单_${this.orderData.no || this.orderId}_${new Date().getTime()}.pdf`

      // 显示加载提示
      const loading = this.showLoading('正在生成简化版PDF...')

      try {
        // 添加PDF模式样式
        previewContainer.classList.add('pdf-mode')

        // 等待样式应用
        await new Promise(resolve => setTimeout(resolve, 100))

        // 创建PDF文档
        const pdf = new jsPDF({
          orientation: 'portrait',
          unit: 'mm',
          format: 'a4'
        })

        // 转换HTML为Canvas
        const canvas = await html2canvas(previewContainer, {
          scale: 1,
          useCORS: true,
          allowTaint: true,
          backgroundColor: '#ffffff',
          logging: false
        })

        // 计算尺寸
        const pageWidth = pdf.internal.pageSize.getWidth()
        const pageHeight = pdf.internal.pageSize.getHeight()
        const margin = 10

        // 计算缩放比例
        const imgWidth = pageWidth - (margin * 2)
        const imgHeight = (canvas.height * imgWidth) / canvas.width

        // 如果内容超过一页，需要分页
        if (imgHeight > pageHeight - (margin * 2)) {
          const pages = Math.ceil(imgHeight / (pageHeight - (margin * 2)))

          for (let i = 0; i < pages; i++) {
            if (i > 0) {
              pdf.addPage()
            }

            const sourceY = i * (pageHeight - (margin * 2)) * (canvas.height / imgHeight)
            const sourceHeight = Math.min((pageHeight - (margin * 2)) * (canvas.height / imgHeight), canvas.height - sourceY)

            // 创建临时canvas
            const tempCanvas = document.createElement('canvas')
            const tempCtx = tempCanvas.getContext('2d')
            tempCanvas.width = canvas.width
            tempCanvas.height = sourceHeight

            tempCtx.drawImage(canvas, 0, sourceY, canvas.width, sourceHeight, 0, 0, canvas.width, sourceHeight)

            const imgData = tempCanvas.toDataURL('image/jpeg', 0.8)
            pdf.addImage(imgData, 'JPEG', margin, margin, imgWidth, (sourceHeight * imgWidth) / canvas.width)
          }
        } else {
          // 单页处理
          const imgData = canvas.toDataURL('image/jpeg', 0.8)
          pdf.addImage(imgData, 'JPEG', margin, margin, imgWidth, imgHeight)
        }

        // 隐藏加载提示
        this.hideLoading(loading)

        // 移除PDF模式样式
        previewContainer.classList.remove('pdf-mode')

        // 下载PDF
        pdf.save(filename)
      } catch (error) {
        this.hideLoading(loading)
        // 移除PDF模式样式
        previewContainer.classList.remove('pdf-mode')
        throw error
      }
    },

    // 显示加载提示
    showLoading(message = '正在处理...') {
      // 创建加载遮罩
      const loadingMask = document.createElement('div')
      loadingMask.style.cssText = `
        position: fixed;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background: rgba(0, 0, 0, 0.5);
        display: flex;
        justify-content: center;
        align-items: center;
        z-index: 9999;
      `

      // 创建加载内容
      const loadingContent = document.createElement('div')
      loadingContent.style.cssText = `
        background: white;
        padding: 20px 30px;
        border-radius: 8px;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
        text-align: center;
      `

      // 创建加载图标
      const loadingIcon = document.createElement('div')
      loadingIcon.style.cssText = `
        width: 32px;
        height: 32px;
        border: 3px solid #f3f3f3;
        border-top: 3px solid #409eff;
        border-radius: 50%;
        animation: spin 1s linear infinite;
        margin: 0 auto 10px;
      `

      // 创建加载文字
      const loadingText = document.createElement('div')
      loadingText.textContent = message
      loadingText.style.cssText = `
        color: #606266;
        font-size: 14px;
      `

      // 添加CSS动画
      const style = document.createElement('style')
      style.textContent = `
        @keyframes spin {
          0% { transform: rotate(0deg); }
          100% { transform: rotate(360deg); }
        }
      `
      document.head.appendChild(style)

      // 组装加载元素
      loadingContent.appendChild(loadingIcon)
      loadingContent.appendChild(loadingText)
      loadingMask.appendChild(loadingContent)
      document.body.appendChild(loadingMask)

      return loadingMask
    },

    // 隐藏加载提示
    hideLoading(loadingElement) {
      if (loadingElement && loadingElement.parentNode) {
        loadingElement.parentNode.removeChild(loadingElement)
      }
    },

    // 预览PDF
    previewPDF() {
      this.$message.info('预览功能开发中...')
      // 这里可以实现PDF预览功能
      // 可以使用PDF.js或其他PDF预览库
    },

    // 打印PDF
    printPDF() {
      const previewContainer = this.$refs.previewContainer
      if (!previewContainer) {
        this.$message.error('预览容器不存在')
        return
      }

      // 创建打印窗口
      const printWindow = window.open('', '_blank')
      const printContent = previewContainer.innerHTML

      printWindow.document.write(`
        <!DOCTYPE html>
        <html>
        <head>
          <title>业务申请单打印</title>
          <style>
            * {
              box-sizing: border-box;
            }
            
            body {
              margin: 0;
              padding: 20mm;
              font-family: SimSun, serif;
              font-size: 12px;
              line-height: 1.5;
              color: #333;
              background: white;
            }
            
            .application-preview {
              padding: 0;
            }
            
            .application-title {
              text-align: center;
              margin-bottom: 30px;
            }
            
            .application-title h1 {
              font-size: 28px;
              font-weight: bold;
              margin: 0;
            }
            
            .contract-info {
              margin-bottom: 30px;
            }
            
            .contract-parties {
              display: flex;
              flex-direction: column;
              gap: 10px;
            }
            
            .party-item {
              display: flex;
              align-items: center;
            }
            
            .party-item .label {
              font-weight: bold;
              min-width: 80px;
            }
            
            .party-item .value {
              margin-left: 10px;
            }
            
            .application-description {
              text-align: center;
              margin-bottom: 30px;
              font-size: 14px;
            }
            
            .section-title {
              font-size: 18px;
              font-weight: bold;
              margin: 25px 0 15px 0;
              padding: 8px 0;
              border-bottom: 1px solid #ccc;
            }
            
            .order-details-table {
              margin-bottom: 20px;
            }
            
            .order-details-table table {
              width: 100%;
              border-collapse: collapse;
              border: 1px solid #ccc;
            }
            
            .order-details-table th,
            .order-details-table td {
              border: 1px solid #ccc;
              padding: 8px;
              text-align: center;
            }
            
            .order-details-table th {
              background-color: #f5f5f5;
              font-weight: bold;
            }
            
            .notes-section {
              margin-top: 20px;
            }
            
            .notes-text {
              font-size: 12px;
              color: #666;
              margin: 0;
            }
            
            .info-item {
              display: flex;
              align-items: flex-start;
              margin-bottom: 10px;
              line-height: 1.6;
            }
            
            .info-item .label {
              font-weight: bold;
              min-width: 200px;
              margin-right: 10px;
            }
            
            .info-item .value {
              flex: 1;
            }
            
            .company-info-footer {
              margin-top: 50px;
              text-align: right;
            }
            
            .company-info-footer .company-name {
              font-size: 16px;
              font-weight: bold;
              margin-bottom: 10px;
            }
            
            .company-info-footer .application-date {
              font-size: 12px;
              color: #666;
            }
            
            .final-notes-section {
              margin-top: 30px;
              margin-bottom: 30px;
            }
            
            .final-notes-section .final-notes-text {
              font-size: 12px;
              color: #666;
              line-height: 1.6;
              margin: 0;
            }
            
            @media print {
              body {
                padding: 10mm;
              }
              
              .application-preview {
                page-break-inside: avoid;
              }
              
              .section-title {
                page-break-after: avoid;
              }
              
              .order-details-table {
                page-break-inside: avoid;
              }
            }
          </style>
        </head>
        <body>
          ${printContent}
        </body>
        </html>
      `)

      printWindow.document.close()
      printWindow.focus()

      // 等待内容加载完成后打印
      printWindow.onload = () => {
        printWindow.print()
        printWindow.close()
      }
    },

    // 刷新数据
    refreshData() {
      this.$message.success('数据已刷新')
      this.loadOrderData()
    },

    // 格式化日期时间
    formatDateTime(time) {
      return time ? parseTime(time) : '--'
    },

    // 格式化日期
    formatDate(date) {
      if (!date) return '--'
      const d = new Date(date)
      return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
    },

    // 获取订单类型显示文本
    getOrderTypeText(type) {
      const textMap = {
        'purchase': '采购',
        'transport': '运输',
        'process': '加工',
        'sales': '销售',
        'storage': '仓储',
        'other': '其他'
      }
      return textMap[type] || '未知类型'
    },

    // 金额格式化
    formatAmount(amount) {
      const num = Number(amount) || 0
      return num.toLocaleString(undefined, { minimumFractionDigits: 0, maximumFractionDigits: 2 })
    }
  }
}
</script>

<style lang="scss" scoped>
.application-pdf-page {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;

  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    padding: 20px;
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);

    .header-left {
      display: flex;
      align-items: center;
      gap: 15px;

      h2 {
        margin: 0;
        color: #303133;
      }
    }

    .header-right {
      display: flex;
      gap: 10px;
      flex-wrap: wrap;
    }
  }

  .preview-container {
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    overflow: hidden;

    &.pdf-mode {
      border-radius: 0;
      box-shadow: none;
      background: white;

      .application-preview {
        padding: 20px;
        font-size: 11px;

        .application-title h1 {
          font-size: 24px;
        }

        .section-title {
          font-size: 16px;
        }

        table {
          font-size: 10px;

          th, td {
            padding: 6px;
          }
        }
      }
    }

    .application-preview {
      padding: 40px;
      font-family: 'SimSun', 'Microsoft YaHei', 'PingFang SC', serif;
      font-size: 12px;
      line-height: 1.5;
      color: #333;
      background: white;
      min-height: 100%;

      .application-title {
        text-align: center;
        margin-bottom: 30px;

        h1 {
          font-size: 28px;
          font-weight: bold;
          margin: 0;
        }
      }

      .contract-info {
        margin-bottom: 30px;

        .contract-parties {
          display: flex;
          flex-direction: column;
          gap: 10px;

          .party-item {
            display: flex;
            align-items: center;

            .label {
              font-weight: bold;
              min-width: 80px;
            }

            .value {
              margin-left: 10px;
            }
          }
        }
      }

      .application-description {
        text-align: center;
        margin-bottom: 30px;
        font-size: 14px;

        p {
          margin: 0;
        }
      }

      .section-title {
        font-size: 18px;
        font-weight: bold;
        margin: 25px 0 15px 0;
        padding: 8px 0;
        border-bottom: 1px solid #ccc;
      }

      .order-details-table {
        margin-bottom: 20px;

        table {
          width: 100%;
          border-collapse: collapse;
          border: 1px solid #ccc;

          th, td {
            border: 1px solid #ccc;
            padding: 8px;
            text-align: center;
          }

          th {
            background-color: #f5f5f5;
            font-weight: bold;
          }
        }
      }

      .notes-section {
        margin-top: 20px;

        .notes-text {
          font-size: 12px;
          color: #666;
          margin: 0;
        }
      }

      .info-item {
        display: flex;
        align-items: flex-start;
        margin-bottom: 10px;
        line-height: 1.6;

        .label {
          font-weight: bold;
          min-width: 200px;
          margin-right: 10px;
        }

        .value {
          flex: 1;
        }
      }

      .company-info-footer {
        margin-top: 50px;
        text-align: right;

        .company-name {
          font-size: 16px;
          font-weight: bold;
          margin-bottom: 10px;
        }

        .application-date {
          font-size: 12px;
          color: #666;
        }
      }

      .final-notes-section {
        margin-top: 30px;
        margin-bottom: 30px;

        .final-notes-text {
          font-size: 12px;
          color: #666;
          line-height: 1.6;
          margin: 0;
        }
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .application-pdf-page {
    padding: 10px;

    .page-header {
      flex-direction: column;
      gap: 15px;
      align-items: stretch;

      .header-left {
        justify-content: center;
      }

      .header-right {
        justify-content: center;
      }
    }

    .preview-container {
      .application-preview {
        padding: 20px;

        .application-title h1 {
          font-size: 24px;
        }

        .contract-info {
          .contract-parties {
            .party-item {
              flex-direction: column;
              align-items: flex-start;
              gap: 5px;

              .label {
                min-width: auto;
              }

              .value {
                margin-left: 0;
              }
            }
          }
        }

        .info-item {
          flex-direction: column;
          align-items: flex-start;
          gap: 5px;

          .label {
            min-width: auto;
            margin-right: 0;
          }
        }

        .order-details-table {
          overflow-x: auto;

          table {
            min-width: 600px;
          }
        }
      }
    }
  }
}
</style>
