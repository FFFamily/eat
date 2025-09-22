-- 回收发票示例数据

-- 插入示例发票数据
INSERT INTO `recycle_invoice` (`id`, `invoice_no`, `invoice_type`, `invoice_bank`, `planned_invoice_time`, `status`, `processor`, `invoice_time`, `total_amount`, `tax_amount`, `amount_without_tax`, `create_time`, `update_time`, `create_by`, `update_by`, `is_deleted`) VALUES
('invoice001', 'INV20240101001', '销项', '中国工商银行', '2024-01-15 10:00:00', 'pending', '张三', NULL, 1000.00, 130.00, 870.00, '2024-01-01 10:00:00', '2024-01-01 10:00:00', 'admin', 'admin', '0'),
('invoice002', 'INV20240101002', '进项', '中国建设银行', '2024-01-16 14:00:00', 'invoiced', '李四', '2024-01-16 14:30:00', 2000.00, 260.00, 1740.00, '2024-01-02 09:00:00', '2024-01-16 14:30:00', 'admin', 'admin', '0'),
('invoice003', 'INV20240101003', '销项', '中国农业银行', '2024-01-20 16:00:00', 'pending', '王五', NULL, 1500.00, 195.00, 1305.00, '2024-01-03 11:00:00', '2024-01-03 11:00:00', 'admin', 'admin', '0');

-- 插入示例发票明细数据
INSERT INTO `recycle_invoice_detail` (`id`, `invoice_id`, `order_no`, `order_total_amount`, `order_actual_invoice`, `order_should_invoice`, `create_time`, `update_time`, `create_by`, `update_by`, `is_deleted`) VALUES
('detail001', 'invoice001', 'RO20240101001', 500.00, 500.00, 500.00, '2024-01-01 10:00:00', '2024-01-01 10:00:00', 'admin', 'admin', '0'),
('detail002', 'invoice001', 'RO20240101002', 500.00, 500.00, 500.00, '2024-01-01 10:00:00', '2024-01-01 10:00:00', 'admin', 'admin', '0'),
('detail003', 'invoice002', 'RO20240102001', 1000.00, 1000.00, 1000.00, '2024-01-02 09:00:00', '2024-01-02 09:00:00', 'admin', 'admin', '0'),
('detail004', 'invoice002', 'RO20240102002', 1000.00, 1000.00, 1000.00, '2024-01-02 09:00:00', '2024-01-02 09:00:00', 'admin', 'admin', '0'),
('detail005', 'invoice003', 'RO20240103001', 750.00, 750.00, 750.00, '2024-01-03 11:00:00', '2024-01-03 11:00:00', 'admin', 'admin', '0'),
('detail006', 'invoice003', 'RO20240103002', 750.00, 750.00, 750.00, '2024-01-03 11:00:00', '2024-01-03 11:00:00', 'admin', 'admin', '0');
