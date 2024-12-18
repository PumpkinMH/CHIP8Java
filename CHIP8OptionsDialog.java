import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;

public class CHIP8OptionsDialog extends JDialog {

  private JPanel contentPane;
  private JButton buttonOK;
  private JButton buttonCancel;
  private JPanel contentPanel;
  private JPanel optionsPanel;
  private JLabel ScaleLabel;
  private JSpinner ScaleSpinner;
  private JLabel DelayLabel;
  private JSpinner DelaySpinner;

  public CHIP8OptionsDialog() {
    setContentPane(contentPane);
    setModal(true);
    getRootPane().setDefaultButton(buttonOK);

    buttonOK.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        onOK();
      }
    });

    buttonCancel.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        onCancel();
      }
    });

    // call onCancel() when cross is clicked
    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        onCancel();
      }
    });

    // call onCancel() on ESCAPE
    contentPane.registerKeyboardAction(new ActionListener() {
                                         public void actionPerformed(ActionEvent e) {
                                           onCancel();
                                         }
                                       }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
        JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
  }

  private void onOK() {
    // add your code here
    dispose();
  }

  private void onCancel() {
    // add your code here if necessary
    dispose();
  }

  public static void main(String[] args) {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception ingored) {

    }
    CHIP8OptionsDialog dialog = new CHIP8OptionsDialog();
    dialog.setTitle("CHIP-8 Options");
    dialog.pack();
    dialog.setVisible(true);
    System.exit(0);
  }

  private void createUIComponents() {
    SpinnerNumberModel scaleModel = new SpinnerNumberModel(1,1,null,1);
    ScaleSpinner = new JSpinner(scaleModel);

    SpinnerNumberModel delayModel = new SpinnerNumberModel(1,0,null,1);
    DelaySpinner = new JSpinner(delayModel);

    // TODO: place custom component creation code here
  }
}
