<template>
  <div class="settlement-pdf-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <el-button icon="el-icon-arrow-left" @click="goBack">返回</el-button>
        <h2>结算单预览</h2>
      </div>
      <div class="header-right">
        <el-button type="primary" icon="el-icon-download" :loading="generating" @click="generatePDF">
          生成PDF
        </el-button>
        <el-button type="warning" icon="el-icon-printer" @click="printPDF">
          打印
        </el-button>
        <el-button icon="el-icon-refresh" @click="refreshData">
          刷新数据
        </el-button>
      </div>
    </div>

    <!-- 结算单预览 -->
    <div ref="previewContainer" class="preview-container">
      <div class="settlement-preview">
        <!-- 结算单标题 -->
        <div class="settlement-title">
          <h1>订单结算单</h1>
        </div>

        <!-- 合同基本信息 -->
        <div class="contract-info">
          <div class="contract-parties">
            <div class="party-item">
              <span class="label">甲方：</span>
              <span class="value">{{ orderData.partyAName }}</span>
            </div>
            <div class="party-item">
              <span class="label">乙方：</span>
              <span class="value">{{ orderData.partyBName }}</span>
            </div>
            <div class="party-item">
              <span class="label">合同编号：</span>
              <span class="value">{{ orderData.contractNo }}</span>
            </div>
          </div>
        </div>

        <!-- 结算说明 -->
        <div class="settlement-description">
          <p>现就该合同下编号为 <strong>{{ orderData.no }}</strong> 的订单出具以下业务结算单</p>
        </div>

        <!-- 订单详细信息 -->
        <div class="order-details-info">
          <h2 class="section-title">订单详细信息</h2>
          <div class="info-grid">
            <div class="info-item">
              <span class="label">订单类型：</span>
              <span class="value">{{ getOrderTypeText(orderData.type) }}</span>
            </div>
            <div class="info-item">
              <span class="label">交付地址：</span>
              <span class="value">{{ orderData.deliveryAddress || '--' }}</span>
            </div>
            <div class="info-item">
              <span class="label">交付货物送达我司地址：</span>
              <span class="value">{{ orderData.warehouseAddress || '--' }}</span>
            </div>
            <div class="info-item">
              <span class="label">交付服务计入我司成本：</span>
              <span class="value">是</span>
            </div>
            <div class="info-item">
              <span class="label">订单开始时间：</span>
              <span class="value">{{ formatDateTime(orderData.startTime) }}</span>
            </div>
            <div class="info-item">
              <span class="label">订单结束时间：</span>
              <span class="value">{{ formatDateTime(orderData.endTime) }}</span>
            </div>
            <div class="info-item">
              <span class="label">订单经办人：</span>
              <span class="value">{{ orderData.processor || '--' }}</span>
            </div>
          </div>
        </div>

        <!-- 常规部分 -->
        <div class="regular-section">
          <h2 class="section-title">常规部分</h2>

          <!-- 订单明细表格 -->
          <div class="order-details-table">
            <table>
              <thead>
                <tr>
                  <th>序号</th>
                  <th>类型</th>
                  <th>名称</th>
                  <th>规格型号</th>
                  <th>数量</th>
                  <th>单价</th>
                  <th>金额</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(item, index) in orderItems" :key="index">
                  <td>{{ index + 1 }}</td>
                  <td>{{ getItemTypeText(item.type) }}</td>
                  <td>{{ item.name }}</td>
                  <td>{{ item.specification }}</td>
                  <td>{{ item.quantity }}</td>
                  <td>¥{{ formatAmount(item.unitPrice) }}</td>
                  <td>¥{{ formatAmount(item.amount) }}</td>
                </tr>
              </tbody>
              <tfoot>
                <tr class="total-row">
                  <td colspan="4" class="total-label">合计</td>
                  <td>{{ totalQuantity }}</td>
                  <td>¥{{ formatAmount(averageUnitPrice) }}</td>
                  <td>¥{{ formatAmount(totalAmount) }}</td>
                </tr>
              </tfoot>
            </table>
          </div>

          <!-- 批注说明 -->
          <div class="notes-section">
            <p class="notes-text">
              <strong>批注：</strong>货物类数量单位为kg、运输类数量单位为kg，总价单位为元
            </p>
          </div>
        </div>

        <!-- 调价部分 -->
        <div class="adjustment-section">
          <h2 class="section-title">调价部分</h2>
          <div class="adjustment-content">
            <p class="adjustment-summary">
              此结算单调价共调价 <strong>{{ formatAmount(totalAdjustment) }}</strong> 元
            </p>
            <div class="adjustment-details">
              <div class="adjustment-item">
                <span class="adjustment-number">（1）</span>
                <span class="adjustment-label">合作评级：</span>
                <span class="adjustment-value">评级{{ ratingLevel }}，总金额调整{{ formatAmount(ratingAdjustment) }}，调价{{ formatAmount(ratingAdjustment) }}元</span>
              </div>
              <div class="adjustment-item">
                <span class="adjustment-number">（2）</span>
                <span class="adjustment-label">其他调价：</span>
                <span class="adjustment-value">调价{{ formatAmount(otherAdjustment) }}元</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 结算金额部分 -->
        <div class="settlement-amount-section">
          <h2 class="section-title">结算金额</h2>
          <div class="settlement-content">
            <p class="settlement-summary">
              该笔订单总数 <strong>{{ totalQuantity }}</strong> {{ getQuantityUnit() }}，待结金额 <strong>{{ formatAmount(pendingAmount) }}</strong> 元，综合单价 <strong>{{ formatAmount(averageUnitPrice) }}</strong> 元/{{ getQuantityUnit() }}
            </p>
            <p class="settlement-amount-words">
              待结金额大写：<strong>{{ amountToChinese(pendingAmount) }}</strong>
            </p>
            <p class="settlement-account">
              约定走款账号：<strong>{{ orderData.paymentAccount || '6222021234567890123' }}</strong>
            </p>
            <p class="settlement-request">
              请您核算以上数据，我司将据此结算。
            </p>
          </div>
        </div>

        <!-- 最终批注 -->
        <div class="final-notes-section">
          <p class="final-notes-text">
            如有疑问请于本单据发出后3个工作日内联系我司企业微信客服或经办人员 <strong>孟经理（13683335083）</strong>，过时默认同意本结算单内容。
          </p>
        </div>

        <!-- 公司信息和日期 -->
        <div class="company-info-footer">
          <div class="company-name">云南伏宁再生资源有限公司</div>
          <div class="settlement-date">结算单生成日期：{{ formatDate(new Date()) }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { generateSettlementPDF, getRecycleDetail } from '@/api/recycle'
import { getUserOrderInfo } from '@/api/userOrder'
import { parseTime } from '@/utils'
import {
  getOrderTypeText,
  getOrderStatusText
} from '@/constants/orderTypes'

export default {
  name: 'SettlementPDF',
  data() {
    return {
      orderId: null,
      orderType: 'recycle',
      orderData: {},
      orderItems: [],
      generating: false
    }
  },
  computed: {
    // 计算总数量
    totalQuantity() {
      return this.orderItems.reduce((sum, item) => sum + (Number(item.quantity) || 0), 0)
    },
    // 计算平均单价
    averageUnitPrice() {
      if (this.orderItems.length === 0) return 0
      const totalUnitPrice = this.orderItems.reduce((sum, item) => sum + (Number(item.unitPrice) || 0), 0)
      return totalUnitPrice / this.orderItems.length
    },
    // 计算总金额
    totalAmount() {
      return this.orderItems.reduce((sum, item) => sum + (Number(item.amount) || 0), 0)
    },
    // 计算合作评级调价金额：(货物单价*数量*重量)*评级系数
    ratingAdjustment() {
      if (!this.orderData.items || this.orderData.items.length === 0) {
        return 0
      }

      return this.orderData.items.reduce((sum, item) => {
        const unitPrice = Number(item.goodPrice) || 0
        const quantity = Number(item.goodCount) || 0
        const weight = Number(item.goodWeight) || 0
        const ratingCoefficient = Number(item.ratingCoefficient) || 0

        // 合作评级调价 = (货物单价 * 数量 * 重量) * 评级系数
        const baseAmount = unitPrice * quantity * weight
        const ratingAdjust = baseAmount * ratingCoefficient

        return sum + ratingAdjust
      }, 0)
    },
    // 计算其他调价金额：直接汇总订单明细中的其他调价
    otherAdjustment() {
      if (!this.orderData.items || this.orderData.items.length === 0) {
        return 0
      }

      return this.orderData.items.reduce((sum, item) => {
        const otherAdjust = Number(item.otherAdjustAmount) || 0
        return sum + otherAdjust
      }, 0)
    },
    // 计算总调价金额
    totalAdjustment() {
      return this.ratingAdjustment + this.otherAdjustment
    },
    // 计算评级等级（基于平均评级系数）
    ratingLevel() {
      if (!this.orderData.items || this.orderData.items.length === 0) {
        return 'A'
      }

      // 计算平均评级系数
      const totalRating = this.orderData.items.reduce((sum, item) => {
        return sum + (Number(item.ratingCoefficient) || 0)
      }, 0)

      const avgRating = totalRating / this.orderData.items.length

      // 根据评级系数范围确定等级
      if (avgRating >= 0.1) return 'A+'
      if (avgRating >= 0.05) return 'A'
      if (avgRating >= 0) return 'B'
      if (avgRating >= -0.05) return 'C'
      return 'D'
    },
    // 计算待结算金额：货物总价 + 调价总价
    pendingAmount() {
      return this.totalAmount + this.totalAdjustment
    }
  },
  created() {
    // 从路由参数获取订单ID和类型
    this.orderId = this.$route.params.orderId || this.$route.query.orderId
    this.orderType = this.$route.query.type || 'recycle'

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
        let response
        if (this.orderType === 'userOrder') {
          // 用户订单
          response = await getUserOrderInfo(this.orderId)
          const data = response.data || {}
          const userOrder = data.userOrder || {}
          const storageOrder = data.storageOrder || {}

          // 构建订单数据
          this.orderData = {
            no: userOrder.no || '',
            partyAName: userOrder.partyAName || '',
            partyBName: userOrder.partyBName || '',
            contractNo: userOrder.contractNo || '',
            deliveryAddress: storageOrder.deliveryAddress || userOrder.deliveryAddress || '',
            warehouseAddress: storageOrder.warehouseName || '',
            startTime: userOrder.createTime || '',
            endTime: userOrder.settlementTime || '',
            processor: storageOrder.processorName || userOrder.processorName || '',
            paymentAccount: userOrder.paymentAccount || '',
            items: storageOrder.items || []
          }

          // 使用入库阶段的明细
          if (this.orderData.items && this.orderData.items.length > 0) {
            this.orderItems = this.orderData.items.map((item, index) => {
              const quantity = Number(item.goodCount) || 0
              const unitPrice = Number(item.goodPrice) || 0

              return {
                type: item.goodType || '货物',
                name: item.goodName || '商品' + (index + 1),
                specification: item.goodModel || '标准规格',
                quantity: quantity,
                unitPrice: unitPrice,
                amount: quantity * unitPrice
              }
            })
          }
        } else {
          // 回收订单
          response = await getRecycleDetail(this.orderId)
          this.orderData = response.data || {}

          // 将API返回的items转换为PDF需要的格式
          if (this.orderData.items && this.orderData.items.length > 0) {
            this.orderItems = this.orderData.items.map((item, index) => {
              const quantity = Number(item.goodCount) || 0
              const unitPrice = Number(item.goodPrice) || 0

              return {
                type: item.goodType || '货物',
                name: item.goodName || '商品' + (index + 1),
                specification: item.goodModel || '标准规格',
                quantity: quantity,
                unitPrice: unitPrice,
                amount: quantity * unitPrice
              }
            })
          }
        }
      } catch (error) {
        console.error('获取订单数据失败:', error)
        this.$message.error('获取订单数据失败')
      }
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

    // 返回上一页
    goBack() {
      this.$router.go(-1)
    },

    // 生成PDF
    async generatePDF() {
      try {
        this.generating = true

        // 方法1：使用后端API生成PDF
        // await this.generatePDFFromAPI()

        // 方法2：使用前端生成PDF（备用方案）
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

    // 使用后端API生成PDF
    async generatePDFFromAPI() {
      try {
        const response = await generateSettlementPDF(this.orderId)

        // 创建下载链接
        const blob = new Blob([response.data], { type: 'application/pdf' })
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        link.download = `结算单_${this.orderData.no || this.orderId}_${new Date().getTime()}.pdf`
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        window.URL.revokeObjectURL(url)
      } catch (error) {
        console.error('后端PDF生成失败，使用前端生成:', error)
        // 如果后端失败，使用前端生成
        await this.generatePDFFromFrontend()
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

      const filename = `结算单_${this.orderData.no || this.orderId}_${new Date().getTime()}.pdf`

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
          this.addPDFHeader(pdf, '订单结算单', pageWidth, margin)

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

      const filename = `结算单_${this.orderData.no || this.orderId}_${new Date().getTime()}.pdf`

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
          <title>结算单打印</title>
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
            
            .settlement-preview {
              padding: 0;
            }
            
            .settlement-title {
              text-align: center;
              margin-bottom: 30px;
            }
            
            .settlement-title h1 {
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
            
            .settlement-description {
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
            
            .order-details-table .total-row {
              background-color: #f9f9f9;
              font-weight: bold;
            }
            
            .order-details-table .total-label {
              text-align: right;
            }
            
            .notes-section {
              margin-top: 20px;
            }
            
            .notes-text {
              font-size: 12px;
              color: #666;
              margin: 0;
            }
            
            .order-details-info {
              margin-bottom: 30px;
            }
            
            .order-details-info .info-grid {
              display: grid;
              grid-template-columns: 1fr 1fr;
              gap: 15px;
            }
            
            .order-details-info .info-item {
              display: flex;
              align-items: center;
            }
            
            .order-details-info .info-item .label {
              font-weight: bold;
              min-width: 180px;
            }
            
            .order-details-info .info-item .value {
              margin-left: 10px;
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
            
            .company-info-footer .settlement-date {
              font-size: 12px;
              color: #666;
            }
            
            .adjustment-section {
              margin-top: 30px;
            }
            
            .adjustment-section .adjustment-content {
              margin-bottom: 20px;
            }
            
            .adjustment-section .adjustment-summary {
              font-size: 14px;
              margin-bottom: 15px;
              text-align: left;
            }
            
            .adjustment-section .adjustment-details {
              margin-left: 20px;
            }
            
            .adjustment-section .adjustment-item {
              display: flex;
              align-items: flex-start;
              margin-bottom: 10px;
              line-height: 1.6;
            }
            
            .adjustment-section .adjustment-number {
              font-weight: bold;
              margin-right: 8px;
              min-width: 30px;
            }
            
            .adjustment-section .adjustment-label {
              font-weight: bold;
              margin-right: 8px;
              min-width: 80px;
            }
            
            .adjustment-section .adjustment-value {
              flex: 1;
            }
            
            .settlement-amount-section {
              margin-top: 30px;
            }
            
            .settlement-amount-section .settlement-content {
              margin-bottom: 20px;
            }
            
            .settlement-amount-section .settlement-summary {
              font-size: 14px;
              margin-bottom: 15px;
              line-height: 1.6;
            }
            
            .settlement-amount-section .settlement-amount-words {
              font-size: 14px;
              margin-bottom: 15px;
              line-height: 1.6;
            }
            
            .settlement-amount-section .settlement-account {
              font-size: 14px;
              margin-bottom: 15px;
              line-height: 1.6;
            }
            
            .settlement-amount-section .settlement-request {
              font-size: 14px;
              margin-bottom: 15px;
              line-height: 1.6;
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
              
              .settlement-preview {
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
        'purchase': '采购订单',
        'transport': '运输订单',
        'process': '加工订单',
        'sales': '销售订单',
        'storage': '仓储订单',
        'other': '其他订单'
      }
      return textMap[type] || '未知类型'
    },

    // 获取订单状态显示文本
    getOrderStatusText(status) {
      const textMap = {
        'processing': '执行中',
        'completed': '已结算'
      }
      return textMap[status] || '未知状态'
    },

    // 金额格式化
    formatAmount(amount) {
      const num = Number(amount) || 0
      return num.toLocaleString(undefined, { minimumFractionDigits: 0, maximumFractionDigits: 2 })
    },

    // 获取数量单位
    getQuantityUnit() {
      // 根据订单类型返回不同的单位
      const hasTransport = this.orderItems.some(item => item.type === 'transport')
      return hasTransport ? 'km' : 'kg'
    },

    // 金额转中文大写
    amountToChinese(amount) {
      const num = Number(amount) || 0
      if (num === 0) return '零元整'

      const digits = ['零', '壹', '贰', '叁', '肆', '伍', '陆', '柒', '捌', '玖']
      const units = ['', '拾', '佰', '仟']
      const bigUnits = ['', '万', '亿']

      const integerPart = Math.floor(num)
      const decimalPart = Math.round((num - integerPart) * 100)

      let result = ''

      // 处理整数部分
      if (integerPart > 0) {
        const intStr = integerPart.toString()
        const len = intStr.length

        for (let i = 0; i < len; i++) {
          const digit = parseInt(intStr[i])
          const unitIndex = len - 1 - i
          const bigUnitIndex = Math.floor(unitIndex / 4)
          const smallUnitIndex = unitIndex % 4

          if (digit !== 0) {
            result += digits[digit] + units[smallUnitIndex]
          } else if (result && !result.endsWith('零')) {
            result += '零'
          }

          if (smallUnitIndex === 0 && bigUnitIndex > 0 && unitIndex > 0) {
            result += bigUnits[bigUnitIndex]
          }
        }

        result += '元'
      }

      // 处理小数部分
      if (decimalPart > 0) {
        const jiao = Math.floor(decimalPart / 10)
        const fen = decimalPart % 10

        if (jiao > 0) {
          result += digits[jiao] + '角'
        }
        if (fen > 0) {
          result += digits[fen] + '分'
        }
      } else {
        result += '整'
      }

      return result
    }
  }
}
</script>

<style lang="scss" scoped>
.settlement-pdf-page {
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

      .settlement-preview {
        padding: 20px;
        font-size: 11px;

        .settlement-title h1 {
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

    .settlement-preview {
      padding: 40px;
      font-family: 'SimSun', 'Microsoft YaHei', 'PingFang SC', serif;
      font-size: 12px;
      line-height: 1.5;
      color: #333;
      background: white;
      min-height: 100%;

      .settlement-title {
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

      .settlement-description {
        text-align: center;
        margin-bottom: 30px;
        font-size: 14px;

        p {
          margin: 0;
        }
      }

      .order-details-info {
        margin-bottom: 30px;

        .info-grid {
          display: grid;
          grid-template-columns: 1fr 1fr;
          gap: 15px;

          .info-item {
            display: flex;
            align-items: center;

            .label {
              font-weight: bold;
              min-width: 180px;
            }

            .value {
              margin-left: 10px;
            }
          }
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

          .total-row {
            background-color: #f9f9f9;
            font-weight: bold;
          }

          .total-label {
            text-align: right;
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

      .adjustment-section {
        margin-top: 30px;

        .adjustment-content {
          .adjustment-summary {
            font-size: 14px;
            margin-bottom: 15px;
            text-align: left;
          }

          .adjustment-details {
            .adjustment-item {
              display: flex;
              align-items: flex-start;
              margin-bottom: 10px;
              line-height: 1.6;

              .adjustment-number {
                font-weight: bold;
                margin-right: 8px;
                min-width: 30px;
              }

              .adjustment-label {
                font-weight: bold;
                margin-right: 8px;
                min-width: 80px;
              }

                      .adjustment-value {
          flex: 1;
        }

        .company-info-footer {
          text-align: center;

          .company-name {
            font-size: 14px;
          }

          .settlement-date {
            font-size: 11px;
          }
        }
      }
    }
  }
}

      .settlement-amount-section {
        margin-top: 30px;

        .settlement-content {
          .settlement-summary {
            font-size: 14px;
            margin-bottom: 15px;
            line-height: 1.6;
          }

          .settlement-amount-words {
            font-size: 14px;
            margin-bottom: 15px;
            line-height: 1.6;
          }

          .settlement-account {
            font-size: 14px;
            margin-bottom: 15px;
            line-height: 1.6;
          }

          .settlement-request {
            font-size: 14px;
            margin-bottom: 15px;
            line-height: 1.6;
          }
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

      .company-info-footer {
        margin-top: 50px;
        text-align: right;

        .company-name {
          font-size: 16px;
          font-weight: bold;
          margin-bottom: 10px;
        }

        .settlement-date {
          font-size: 12px;
          color: #666;
        }
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .settlement-pdf-page {
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
      .settlement-preview {
        padding: 20px;

        .settlement-title h1 {
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

        .order-details-info {
          .info-grid {
            grid-template-columns: 1fr;
            gap: 10px;

            .info-item {
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

        .order-details-table {
          overflow-x: auto;

          table {
            min-width: 600px;
          }
        }

        .adjustment-section {
          .adjustment-content {
            .adjustment-details {
              .adjustment-item {
                flex-direction: column;
                align-items: flex-start;
                gap: 5px;

                .adjustment-number {
                  margin-right: 0;
                  min-width: auto;
                }

                .adjustment-label {
                  margin-right: 0;
                  min-width: auto;
                }
              }
            }
          }
        }
      }
    }
  }
}
</style>
