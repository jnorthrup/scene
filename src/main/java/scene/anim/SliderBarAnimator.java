package scene.anim;

import scene.SceneLayoutApp;
import scene.gif.AnimatedGifEncoder;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: jim
 * Date: May 15, 2009
 * Time: 1:31:56 AM
 * To change this template use File | Settings | File Templates.
 */
public class SliderBarAnimator {
    final static Map<Class, Class> x = new HashMap();

    static {


        x.put(Boolean.TYPE, Boolean.class);
        x.put(Character.TYPE, Character.class);
        x.put(Byte.TYPE, Byte.class);
        x.put(Short.TYPE, Short.class);
        x.put(Integer.TYPE, Integer.class);
        x.put(Long.TYPE, Long.class);
        x.put(Float.TYPE, Float.class);
        x.put(Double.TYPE, Double.class);
    }

    private JInternalFrame inf;
    private static final String MINIMUM = "Minimum";
    private static final String MAXIMUM = "Maximum";
    private static final String INVERTED = "Inverted";
    private static final String MAJORTICKSPACING = "MajorTickSpacing";
    private static final String MINORTICKSPACING = "MinorTickSpacing";
    private static final String EXTENT = "Extent";
    private static final String ORIENTATION = "Orientation";
    private static final String PAINTTRACK = "PaintTrack";
    private static final String PAINTTICKS = "PaintTicks";
    private static final String PAINTLABELS = "PaintLabels";

    public SliderBarAnimator() {
        inf = new JInternalFrame("Slider Animator");
        SceneLayoutApp.desktopPane.add(inf);
        final ArrayList<Image> frames = new ArrayList<Image>();
                                                 
        final JSlider bar = new JSlider();
        JPanel panel = new JPanel(new BorderLayout());
        inf.setContentPane(panel);

        panel.add(bar, BorderLayout.CENTER);

        final JToolBar t = new JToolBar();
        panel.add(t, BorderLayout.NORTH);

        t.add(new AbstractAction("Record") {
            @Override
            public void actionPerformed(ActionEvent e) {
                 frames.clear();
                bar.setValue(bar.getMinimum());

                while (bar.getValue() < bar.getMaximum()) {
                    bar.setValue(bar.getValue() + 1);
                    Image image = bar.createImage(bar.getWidth(), bar.getHeight());
                    frames.add(image);
                    bar.paint(image.getGraphics());
                }

                JFileChooser chooser = new JFileChooser("/tmp/");

                if (JFileChooser.APPROVE_OPTION == chooser.showSaveDialog(SceneLayoutApp.desktopPane)) {
                    final File selectedFile = chooser.getSelectedFile();

                    final AnimatedGifEncoder gifEncoder = new AnimatedGifEncoder();
                    gifEncoder.setFrameRate(10);
                    gifEncoder.setQuality(20);
                    gifEncoder.setDelay(100);
                    gifEncoder.start(selectedFile.getAbsolutePath());

                    for (Image frame : frames) {
                        final BufferedImage image = (BufferedImage) frame;
                        gifEncoder.addFrame(image);
                    }

                    gifEncoder.finish();
                }
            }
        });

        bar.setMinimum(0);
        bar.setMaximum(100);
        bar.setValue(0);

        final JTextField Minimum = new JTextField("0", 4);

        Minimum.setBorder(BorderFactory.createTitledBorder(MINIMUM));

        t.add(Minimum);

        Minimum.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    final Method[] methods = bar.getClass().getMethods();
                    for (Method method : methods) {
                        String setterName = "set" + MINIMUM;
                        final Class<?>[] parms = method.getParameterTypes();
                        final Object inputValue = Minimum.getText();

                        bind(method, setterName, parms, inputValue, bar);
                        break;

                    }
                } catch (SecurityException ignored) {
                }
            }
        });

        final JTextField Maximum = new JTextField("100", 4);
        Maximum.setBorder(BorderFactory.createTitledBorder(MAXIMUM));
        t.add(Maximum);

        Maximum.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final Method[] methods = bar.getClass().getMethods();
                for (Method method : methods) {
                    String setterName = "set" + MAXIMUM;
                    final Class<?>[] parms = method.getParameterTypes();
                    final Object inputValue = Maximum.getText();

                    bind(method, setterName, parms, inputValue, bar);

                }
            }
        });


        bar.setInverted(false);
        final JCheckBox Inverted = new JCheckBox(INVERTED);
        Inverted.setBorder(BorderFactory.createTitledBorder(INVERTED));
        t.add(Inverted);

        Inverted.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    final Method[] methods = bar.getClass().getMethods();
                    for (Method method : methods) {
                        String setterName = "set" + INVERTED;
                        final Object inputValue = String.valueOf(Inverted.isSelected());
                        final Class<?>[] parms = method.getParameterTypes();
                        bind(method, setterName, parms, inputValue, bar);
                    }
                } catch (SecurityException ignored) {
                }
            }
        }

        );



        bar.setInverted(false);
        final JSpinner Minortickspacing = new JSpinner(     );
        Minortickspacing.setBorder(BorderFactory.createTitledBorder(MINORTICKSPACING));
        t.add(Minortickspacing);

        Minortickspacing.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                try {
                    final Method[] methods = bar.getClass().getMethods();
                    for (Method method : methods) {
                        String setterName = "set" + MINORTICKSPACING;
                        final Object inputValue = String.valueOf(Minortickspacing.getValue());
                        final Class<?>[] parms = method.getParameterTypes();
                        bind(method, setterName, parms, inputValue, bar);
                    }
                } catch (SecurityException ignored) {
                }
            }
        }

        );

        final JSpinner Majortickspacing = new JSpinner();
        Majortickspacing.setBorder(BorderFactory.createTitledBorder(MAJORTICKSPACING));
        t.add(Majortickspacing);

        Majortickspacing.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                try {
                    final Method[] methods = bar.getClass().getMethods();
                    for (Method method : methods) {
                        String setterName = "set" + MAJORTICKSPACING;
                        final Object inputValue = String.valueOf(Majortickspacing.getValue());
                        final Class<?>[] parms = method.getParameterTypes();
                        bind(method, setterName, parms, inputValue, bar);
                    }
                } catch (SecurityException ignored) {
                }
            }
        }

        );                  ;

        final JSpinner Orientation = new JSpinner();
        Orientation.setBorder(BorderFactory.createTitledBorder(ORIENTATION));
        t.add(Orientation);

        Orientation.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                try {
                    final Method[] methods = bar.getClass().getMethods();
                    for (Method method : methods) {
                        String setterName = "set" + ORIENTATION;
                        final Object inputValue = String.valueOf(Orientation.getValue());
                        final Class<?>[] parms = method.getParameterTypes();
                        bind(method, setterName, parms, inputValue, bar);
                    }
                } catch (SecurityException ignored) {
                }
            }
        }

        );                  ;


        final JSpinner Extent = new JSpinner();
        Extent.setBorder(BorderFactory.createTitledBorder(EXTENT));
        t.add(Extent);
        Extent.addChangeListener(new ChangeListener() {  
            @Override
            public void stateChanged(ChangeEvent e) {
                try {
                    final Method[] methods = bar.getClass().getMethods();
                    for (Method method : methods) {
                        String setterName = "set" + EXTENT;
                        final Object inputValue = String.valueOf(Extent.getValue());
                        final Class<?>[] parms = method.getParameterTypes();
                        bind(method, setterName, parms, inputValue, bar);
                    }
                } catch (SecurityException ignored) {
                }
            }
        }

        );                  ;



        final JCheckBox Painttrack = new JCheckBox(PAINTTRACK);
        Painttrack.setBorder(BorderFactory.createTitledBorder(PAINTTRACK));
        t.add(Painttrack);

        Painttrack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    final Method[] methods = bar.getClass().getMethods();
                    for (Method method : methods) {
                        String setterName = "set" + PAINTTRACK;
                        final Object inputValue = String.valueOf(Painttrack.isSelected());
                        final Class<?>[] parms = method.getParameterTypes();
                        bind(method, setterName, parms, inputValue, bar);
                    }
                } catch (SecurityException ignored) {
                }
            }
        }

        );

        final JCheckBox Paintlabels = new JCheckBox(PAINTLABELS);
        Paintlabels.setBorder(BorderFactory.createTitledBorder(PAINTLABELS));
        t.add(Paintlabels);

        Paintlabels.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    final Method[] methods = bar.getClass().getMethods();
                    for (Method method : methods) {
                        String setterName = "set" + PAINTLABELS;
                        final Object inputValue = String.valueOf(Paintlabels.isSelected());
                        final Class<?>[] parms = method.getParameterTypes();
                        bind(method, setterName, parms, inputValue, bar);
                    }
                } catch (SecurityException ignored) {
                }
            }
        }

        );
        final JCheckBox Paintticks = new JCheckBox(PAINTTICKS);
        Paintticks.setBorder(BorderFactory.createTitledBorder(PAINTTICKS));
        t.add(Paintticks);

        Paintticks.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    final Method[] methods = bar.getClass().getMethods();
                    for (Method method : methods) {
                        String setterName = "set" + PAINTTICKS;
                        final Object inputValue = String.valueOf(Paintticks.isSelected());
                        final Class<?>[] parms = method.getParameterTypes();
                        bind(method, setterName, parms, inputValue, bar);
                    }
                } catch (SecurityException ignored) {
                }
            }
        }

        );

        inf.setResizable(true);
        inf.setMaximizable(false);
        inf.setIconifiable(false);
        inf.setClosable(true);
        inf.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
        inf.pack();
        inf.show();

    }

    private static void bind(Method method, String setterName, Class<?>[] parms, Object inputValue, JComponent bar) {
        for (Class<?> parmType : parms) {
            if (method.getName().equals(setterName)) {
                try {
                    parmType = x.containsKey(parmType) ? x.get(parmType) : parmType;
                    final Method valConvertMethod = parmType.getMethod("valueOf", String.class);
                    final Object val = valConvertMethod.invoke(null, inputValue);
                    method.invoke(bar, val);
                    break; //
                } catch (IllegalAccessException e1) {
                    e1.printStackTrace();  //TODO: verify for a purpose
                } catch (IllegalArgumentException e1) {
                    e1.printStackTrace();  //TODO: verify for a purpose
                } catch (InvocationTargetException e1) {
                    e1.printStackTrace();  //TODO: verify for a purpose
                } catch (NoSuchMethodException e1) {
                    e1.printStackTrace();  //TODO: verify for a purpose
                } catch (SecurityException e1) {
                    e1.printStackTrace();  //TODO: verify for a purpose
                }
            }
            break;
        }
    }


    public static void main               (String... a) {
        new SceneLayoutApp();
        new SliderBarAnimator();

    }

}