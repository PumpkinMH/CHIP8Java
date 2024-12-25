import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JCheckBox;
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
  private JSpinner FrequencySpinner;
  private JLabel SoundFrequencyLabel;
  private JCheckBox SoundEnableCheckbox;
  private JLabel SoundEnableLabel;

  private boolean changesMade = false;
  private int scaleValue = 1;
  private int delayValue = 1;
  private int soundFrequency = 440;
  private boolean enableSound = false;

  private static final int MINIMUM_SCALE = 1;
  private static final int MINIMUM_DELAY = 1;
  private static final int MINIMUM_FREQUENCY = 0;

  private static final int MAXIMUM_FREQUENCY = 20000;

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

    this.setTitle("CHIP-8 Options");
  }

  private void onOK() {
    scaleValue = (int) ScaleSpinner.getValue();
    delayValue = (int) DelaySpinner.getValue();
    soundFrequency = (int) FrequencySpinner.getValue();
    enableSound = SoundEnableCheckbox.isSelected();
    changesMade = true;
    dispose();
  }

  private void onCancel() {
    scaleValue = (int) ScaleSpinner.getValue();
    delayValue = (int) DelaySpinner.getValue();
    soundFrequency = (int) FrequencySpinner.getValue();
    enableSound = SoundEnableCheckbox.isSelected();
    changesMade = false;
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

    System.out.println(dialog.getScaleValue() + " " + dialog.getDelayValue());

    System.exit(0);
  }

  private void createUIComponents() {
    SpinnerNumberModel scaleModel = new SpinnerNumberModel(scaleValue,MINIMUM_SCALE,null,1);
    ScaleSpinner = new JSpinner(scaleModel);

    SpinnerNumberModel delayModel = new SpinnerNumberModel(delayValue,MINIMUM_DELAY,null,1);
    DelaySpinner = new JSpinner(delayModel);

    SpinnerNumberModel frequencyModel = new SpinnerNumberModel(soundFrequency, MINIMUM_FREQUENCY, 20000, 1);
    FrequencySpinner = new JSpinner(frequencyModel);
  }

  public void setInitialValues(int scaleValue, int delayValue, int soundFrequency, boolean enableSound) {
    if(scaleValue > MINIMUM_SCALE) {
      ScaleSpinner.setValue(scaleValue);
    }
    if(delayValue > MINIMUM_DELAY) {
      DelaySpinner.setValue(delayValue);
    }
    if(soundFrequency > MINIMUM_FREQUENCY && soundFrequency <= MAXIMUM_FREQUENCY) {
      FrequencySpinner.setValue(soundFrequency);
    }
    SoundEnableCheckbox.setSelected(enableSound);
  }

  public int getScaleValue() {
    return scaleValue;
  }

  public int getDelayValue() {
    return delayValue;
  }

  public int getSoundFrequency() {
    return soundFrequency;
  }

  public boolean isSoundEnabled() {
    return enableSound;
  }

  public boolean isChangesMade() {
    return changesMade;
  }
}
