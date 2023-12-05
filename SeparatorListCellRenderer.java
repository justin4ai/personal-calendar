class SeparatorListCellRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
            boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        if (value != null && value.equals("separator")) {
            label.setPreferredSize(new Dimension(label.getWidth(), 10)); // 구분선 높이 조절
            label.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Color.GRAY)); // 구분선 색상 및 두께 조절
        }

        return label;
    }
}