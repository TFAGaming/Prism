package com.prism.components.frames;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import com.prism.Prism;
import com.prism.utils.ResourceUtil;

public class AboutPrism extends JFrame {

    public Prism prism = Prism.getInstance();

    public String VERSION = prism.getVersion();
    public String AUTHOR = "TFAGaming";
    public String YEAR = "2025";

    public AboutPrism() {
        super("About Prism");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        setIconImage(ResourceUtil.getIcon("icons/Prism.png").getImage());

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFocusable(false);

        tabbedPane.addTab("General", createGeneralPanel());
        tabbedPane.addTab("System", createOperatingSystemPanel());
        tabbedPane.addTab("License", createLicensePanel());
        tabbedPane.addTab("Credits", createCreditsPanel());

        getContentPane().add(tabbedPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setBorder(new EmptyBorder(5, 0, 5, 5));

        JButton okButton = new JButton("OK");
        okButton.setPreferredSize(new Dimension(80, 25));
        okButton.setFocusable(false);
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        buttonPanel.add(okButton, BorderLayout.EAST);

        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);

        setVisible(true);
    }

    private JPanel createGeneralPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel iconLabel = new JLabel();
        iconLabel.setIcon(ResourceUtil.getIcon("icons/Prism.png", 64));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel logoPanel = new JPanel(new BorderLayout());
        logoPanel.add(iconLabel, BorderLayout.CENTER);
        logoPanel.setPreferredSize(new Dimension(80, 80));

        JLabel titleLabel = new JLabel(
                "<html><center><b>Prism</b><br><font size='-1'>Version " + VERSION + "</font></center></html>");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel headerPanel = new JPanel(new BorderLayout(0, 10));
        headerPanel.add(logoPanel, BorderLayout.NORTH);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        JTextPane descriptionArea = new JTextPane();

        StyledDocument doc = descriptionArea.getStyledDocument();

        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);

        try {
            doc.insertString(doc.getLength(), "A robust, open-source, and performant code editor for Windows.\n"
                    + "Built on the reliable foundation of Java Swing,\n"
                    + "it provides quick access and tools for professional use.", null);

            doc.setParagraphAttributes(0, doc.getLength(), center, false);

        } catch (BadLocationException e) {
            WarningDialog.showWarningDialog(prism, e);
        }

        descriptionArea.setCaretColor(descriptionArea.getBackground());
        descriptionArea.setEditable(false);
        descriptionArea.setBackground(panel.getBackground()); // Match panel background
        descriptionArea.setFont(new Font("SansSerif", Font.PLAIN, 12));

        JLabel copyrightLabel = new JLabel("Copyright \u00a9 " + YEAR + " " + AUTHOR + ". All rights reserved.");
        copyrightLabel.setFont(new Font("SansSerif", Font.PLAIN, 10));
        copyrightLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel contentPanel = new JPanel(new BorderLayout(0, 10));
        contentPanel.add(descriptionArea, BorderLayout.CENTER);
        contentPanel.add(copyrightLabel, BorderLayout.SOUTH);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createOperatingSystemPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        String osName = System.getProperty("os.name");
        String osVersion = System.getProperty("os.version");
        String osArch = System.getProperty("os.arch");

        Icon windowsIcon = ResourceUtil.getIcon("icons/windows.png", 64);
        Icon macOSIcon = ResourceUtil.getIcon("icons/macos.png", 64);
        Icon linuxIcon = ResourceUtil.getIcon("icons/linux.png", 64);
        Icon javaIcon = ResourceUtil.getIcon("icons/java2.png", 64);

        JLabel iconLabel = new JLabel();

        if (osName.toLowerCase().contains("windows")) {
            iconLabel.setIcon(windowsIcon);
        } else if (osName.toLowerCase().contains("mac")) {
            iconLabel.setIcon(macOSIcon);
        } else if (osName.toLowerCase().contains("linux")) {
            iconLabel.setIcon(linuxIcon);
        } else {
            iconLabel.setIcon(javaIcon);
        }

        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel logoPanel = new JPanel(new BorderLayout());
        logoPanel.add(iconLabel, BorderLayout.CENTER);
        logoPanel.setPreferredSize(new Dimension(80, 80));

        JLabel titleLabel = new JLabel(
                "<html><center><b>" + osName + "</b><br><font size='-1'>Version " + osVersion + "</font></center></html>");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel headerPanel = new JPanel(new BorderLayout(0, 10));
        headerPanel.add(logoPanel, BorderLayout.NORTH);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        JTextPane descriptionArea = new JTextPane();

        StyledDocument doc = descriptionArea.getStyledDocument();

        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);

        try {
            String desc = "Architecture: {osarch}";

            desc = desc.replace("{osarch}", osArch);

            doc.insertString(doc.getLength(), desc, null);

            doc.setParagraphAttributes(0, doc.getLength(), center, false);

        } catch (BadLocationException e) {
            WarningDialog.showWarningDialog(prism, e);
        }

        descriptionArea.setCaretColor(descriptionArea.getBackground());
        descriptionArea.setEditable(false);
        descriptionArea.setBackground(panel.getBackground()); // Match panel background
        descriptionArea.setFont(new Font("SansSerif", Font.PLAIN, 12));

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(descriptionArea, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createLicensePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        String licenseText = """
                Apache License
                Version 2.0, January 2004
                http://www.apache.org/licenses/

                TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION

                1. Definitions.

                "License" shall mean the terms and conditions for use, reproduction,
                and distribution as defined by Sections 1 through 9 of this document.

                "Licensor" shall mean the copyright owner or entity authorized by
                the copyright owner that is granting the License.

                "Legal Entity" shall mean the union of the acting entity and all
                other entities that control, are controlled by, or are under common
                control with that entity. For the purposes of this definition,
                "control" means (i) the power, direct or indirect, to cause the
                direction or management of such entity, whether by contract or
                otherwise, or (ii) ownership of fifty percent (50%) or more of the
                outstanding shares, or (iii) beneficial ownership of such entity.

                "You" (or "Your") shall mean an individual or Legal Entity
                exercising permissions granted by this License.

                "Source" form shall mean the preferred form for making modifications,
                including but not limited to software source code, documentation
                source, and configuration files.

                "Object" form shall mean any form resulting from mechanical
                transformation or translation of a Source form, including but
                not limited to compiled object code, generated documentation,
                and conversions to other media types.

                "Work" shall mean the work of authorship, whether in Source or
                Object form, made available under the License, as indicated by a
                copyright notice that is included in or attached to the work
                (an example is provided in the Appendix below).

                "Derivative Works" shall mean any work, whether in Source or Object
                form, that is based on (or derived from) the Work and for which the
                editorial revisions, annotations, elaborations, or other modifications
                represent, as a whole, an original work of authorship. For the purposes
                of this License, Derivative Works shall not include works that remain
                separable from, or merely link (or bind by name) to the interfaces of,
                the Work and Derivative Works thereof.

                "Contribution" shall mean any work of authorship, including
                the original version of the Work and any modifications or additions
                to that Work or Derivative Works thereof, that is intentionally
                submitted to Licensor for inclusion in the Work by the copyright owner
                or by an individual or Legal Entity authorized to submit on behalf of
                the copyright owner. For the purposes of this definition, "submitted"
                means any form of electronic, verbal, or written communication sent
                to the Licensor or its representatives, including but not limited to
                communication on electronic mailing lists, source code control systems,
                and issue tracking systems that are managed by, or on behalf of, the
                Licensor for the purpose of discussing and improving the Work, but
                excluding communication that is conspicuously marked or otherwise
                designated in writing by the copyright owner as "Not a Contribution."

                "Contributor" shall mean Licensor and any individual or Legal Entity
                on behalf of whom a Contribution has been received by Licensor and
                subsequently incorporated within the Work.

                2. Grant of Copyright License. Subject to the terms and conditions of
                this License, each Contributor hereby grants to You a perpetual,
                worldwide, non-exclusive, no-charge, royalty-free, irrevocable
                copyright license to reproduce, prepare Derivative Works of,
                publicly display, publicly perform, sublicense, and distribute the
                Work and such Derivative Works in Source or Object form.

                3. Grant of Patent License. Subject to the terms and conditions of
                this License, each Contributor hereby grants to You a perpetual,
                worldwide, non-exclusive, no-charge, royalty-free, irrevocable
                (except as stated in this section) patent license to make, have made,
                use, offer to sell, sell, import, and otherwise transfer the Work,
                where such license applies only to those patent claims licensable
                by such Contributor that are necessarily infringed by their
                Contribution(s) alone or by combination of their Contribution(s)
                with the Work to which such Contribution(s) was submitted. If You
                institute patent litigation against any entity (including a
                cross-claim or counterclaim in a lawsuit) alleging that the Work
                or a Contribution incorporated within the Work constitutes direct
                or contributory patent infringement, then any patent licenses
                granted to You under this License for that Work shall terminate
                as of the date such litigation is filed.

                4. Redistribution. You may reproduce and distribute copies of the
                Work or Derivative Works thereof in any medium, with or without
                modifications, and in Source or Object form, provided that You
                meet the following conditions:

                (a) You must give any other recipients of the Work or
                Derivative Works a copy of this License; and

                (b) You must cause any modified files to carry prominent notices
                stating that You changed the files; and

                (c) You must retain, in the Source form of any Derivative Works
                that You distribute, all copyright, patent, trademark, and
                attribution notices from the Source form of the Work,
                excluding those notices that do not pertain to any part of
                the Derivative Works; and

                (d) If the Work includes a "NOTICE" text file as part of its
                distribution, then any Derivative Works that You distribute must
                include a readable copy of the attribution notices contained
                within such NOTICE file, excluding those notices that do not
                pertain to any part of the Derivative Works, in at least one
                of the following places: within a NOTICE text file distributed
                as part of the Derivative Works; within the Source form or
                documentation, if provided along with the Derivative Works; or,
                within a display generated by the Derivative Works, if and
                wherever such third-party notices normally appear. The contents
                of the NOTICE file are for informational purposes only and
                do not modify the License. You may add Your own attribution
                notices within Derivative Works that You distribute, alongside
                or as an addendum to the NOTICE text from the Work, provided
                that such additional attribution notices cannot be construed
                as modifying the License.

                You may add Your own copyright statement to Your modifications and
                may provide additional or different license terms and conditions
                for use, reproduction, or distribution of Your modifications, or
                for any such Derivative Works as a whole, provided Your use,
                reproduction, and distribution of the Work otherwise complies with
                the conditions stated in this License.

                5. Submission of Contributions. Unless You explicitly state otherwise,
                any Contribution intentionally submitted for inclusion in the Work
                by You to the Licensor shall be under the terms and conditions of
                this License, without any additional terms or conditions.
                Notwithstanding the above, nothing herein shall supersede or modify
                the terms of any separate license agreement you may have executed
                with Licensor regarding such Contributions.

                6. Trademarks. This License does not grant permission to use the trade
                names, trademarks, service marks, or product names of the Licensor,
                except as required for reasonable and customary use in describing the
                origin of the Work and reproducing the content of the NOTICE file.

                7. Disclaimer of Warranty. Unless required by applicable law or
                agreed to in writing, Licensor provides the Work (and each
                Contributor provides its Contributions) on an "AS IS" BASIS,
                WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
                implied, including, without limitation, any warranties or conditions
                of TITLE, NON-INFRINGEMENT, MERCHANTABILITY, or FITNESS FOR A
                PARTICULAR PURPOSE. You are solely responsible for determining the
                appropriateness of using or redistributing the Work and assume any
                risks associated with Your exercise of permissions under this License.

                8. Limitation of Liability. In no event and under no legal theory,
                whether in tort (including negligence), contract, or otherwise,
                unless required by applicable law (such as deliberate and grossly
                negligent acts) or agreed to in writing, shall any Contributor be
                liable to You for damages, including any direct, indirect, special,
                incidental, or consequential damages of any character arising as a
                result of this License or out of the use or inability to use the
                Work (including but not limited to damages for loss of goodwill,
                work stoppage, computer failure or malfunction, or any and all
                other commercial damages or losses), even if such Contributor
                has been advised of the possibility of such damages.

                9. Accepting Warranty or Additional Liability. While redistributing
                the Work or Derivative Works thereof, You may choose to offer,
                and charge a fee for, acceptance of support, warranty, indemnity,
                or other liability obligations and/or rights consistent with this
                License. However, in accepting such obligations, You may act only
                on Your own behalf and on Your sole responsibility, not on behalf
                of any other Contributor, and only if You agree to indemnify,
                defend, and hold each Contributor harmless for any liability
                incurred by, or claims asserted against, such Contributor by reason
                of your accepting any such warranty or additional liability.

                END OF TERMS AND CONDITIONS

                Copyright [YEAR] TFAGaming

                Licensed under the Apache License, Version 2.0 (the "License");
                you may not use this file except in compliance with the License.
                You may obtain a copy of the License at

                http://www.apache.org/licenses/LICENSE-2.0

                Unless required by applicable law or agreed to in writing, software
                distributed under the License is distributed on an "AS IS" BASIS,
                WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
                See the License for the specific language governing permissions and
                limitations under the License.
                                """.replace("[YEAR]", YEAR);

        JTextArea licenseArea = new JTextArea(licenseText);
        licenseArea.setWrapStyleWord(true);
        licenseArea.setLineWrap(true);
        licenseArea.setEditable(false);
        licenseArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));

        JScrollPane scrollPane = new JScrollPane(licenseArea);
        scrollPane.setPreferredSize(new Dimension(450, 250));
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        panel.add(new JLabel("Prism is released under the Apache License 2.0:"), BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createCreditsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        String creditsText = """
                Development Team:
                - TFAGaming (Project Lead)

                Special Thanks:
                - Eclipse, for the toolbar and file icons

                Third-Party Libraries Used:
                - Fifesoft: RSyntaxTextArea - BSD-3-Clause license
                - Fifesoft: Autocomplete - BSD-3-Clause license
                - Java Diff Utils - Apache-2.0 license
                - FasterXML: Jackson databind - Apache-2.0 license
                - Sean Leary: JSON Java (org.json) - Public Domain

                Contact (Discord): tfagaming
                """;

        JTextArea creditsArea = new JTextArea(creditsText);
        creditsArea.setEditable(false);
        creditsArea.setFont(new Font("SansSerif", Font.PLAIN, 12));
        creditsArea.setBackground(panel.getBackground());

        panel.add(new JLabel("Acknowledgements and Information:"), BorderLayout.NORTH);
        panel.add(new JScrollPane(creditsArea), BorderLayout.CENTER);

        return panel;
    }
}
