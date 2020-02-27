package system;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class ResultRender extends JPanel implements ListCellRenderer<Result> {
    private static final long serialVersionUID = 1L;
    private JLabel title = new JLabel();
    private JLabel preview = new JLabel();

    public ResultRender() {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        title.setFont(new Font("Lato", Font.BOLD, 16));
        preview.setFont(new Font("Lato", Font.PLAIN, 14));
        add(title);
        add(preview);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Result> list, Result element, 
            int index, boolean isSelected, boolean cellHasFocus) {
        title.setText(new File(element.getPath()).getName());
        preview.setText(element.getContent().substring(0, 65) + " ...");

        title.setOpaque(true);
        preview.setOpaque(true);

        if (isSelected) {
            title.setBackground(list.getSelectionBackground());
            preview.setBackground(list.getSelectionBackground());
            setBackground(list.getSelectionBackground());
        } else {
            title.setBackground(list.getBackground());
            preview.setBackground(list.getBackground());
            setBackground(list.getBackground());
        }

        return this;
    }
}