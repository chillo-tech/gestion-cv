package tech.chillo.msgestioncv.pdf;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfDocument;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import tech.chillo.msgestioncv.profile.Experience;
import tech.chillo.msgestioncv.profile.Item;
import tech.chillo.msgestioncv.profile.Profile;

import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static com.itextpdf.text.BaseColor.WHITE;
import static com.itextpdf.text.Chunk.NEWLINE;
import static com.itextpdf.text.Element.ALIGN_CENTER;
import static com.itextpdf.text.Font.FontFamily.HELVETICA;
import static com.itextpdf.text.Font.NORMAL;
import static com.itextpdf.text.Rectangle.NO_BORDER;

@Service
public class PDFEditorService {

    public static final BaseColor BLACK = new BaseColor(7, 6, 6);
    public static final BaseColor BLUE = new BaseColor(29, 58, 138);
    public static final BaseColor GREEN = new BaseColor(151, 193, 31);
    public static final Font FONT_10_WHITE = new Font(HELVETICA, 10, NORMAL, WHITE);

    public void generateCV(final Set<Profile> profiles) {
        final Iterator<Profile> profilesIerator = profiles.iterator();
        while (profilesIerator.hasNext()) {
            try {

                final Profile profile = profilesIerator.next();
                final String inits = String.format("%s%s".toUpperCase(), profile.firstName().charAt(0), profile.lastName().charAt(0));

                final Document document = new Document();
                document.setMargins(0, 0, 0, 0);
                final PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(inits + ".pdf"));

                final PdfDocument pdfDoc = new PdfDocument();
                pdfDoc.addWriter(writer);
                pdfDoc.setPageSize(PageSize.A4);

                document.open();

                final PdfPTable table = new PdfPTable(2);
                table.setWidthPercentage(100);
                table.setWidths(new int[]{1, 3});
                table.getDefaultCell().setBorder(NO_BORDER);


                final PdfPCell profileCell = new PdfPCell();
                profileCell.setBorder(NO_BORDER);
                profileCell.setBackgroundColor(BLUE);
                profileCell.setMinimumHeight(PageSize.A4.getHeight());

                final Paragraph initials = new Paragraph(
                        inits,
                        new Font(HELVETICA, 40, Font.BOLD, WHITE)
                );
                initials.setAlignment(ALIGN_CENTER);
                profileCell.addElement(
                        this.contentInTable(
                                initials,
                                Map.of(),
                                new int[]{20, 0, 20, 0}
                        )
                );

                profileCell.addElement(this.getTitle("Formations", Map.of(
                                "width", 0.5F,
                                "color", WHITE
                        ), 13, WHITE,
                        new int[]{10, 0, 0, 0},
                        new int[]{0, 0, 0, 10}
                ));
                profileCell.addElement(this.getItems(profile.formations(), "", new int[]{0, 0, 6, 0}));

                profileCell.addElement(this.getTitle("Compétences", Map.of(
                                "width", 0.5F,
                                "color", WHITE
                        ), 13, WHITE,
                        new int[]{10, 0, 0, 0},
                        new int[]{0, 0, 0, 10}));
                profileCell.addElement(this.getItems(profile.skills(), "- ", new int[]{0, 0, 1, 0}));

                profileCell.addElement(this.getTitle("Langues", Map.of(
                                "width", 0.5F,
                                "color", WHITE
                        ), 13, WHITE,
                        new int[]{10, 0, 0, 0},
                        new int[]{0, 0, 0, 10}));
                profileCell.addElement(this.getItems(profile.languages(), "", new int[]{0, 0, 1, 0}));

                profileCell.addElement(this.getTitle("Intérêts", Map.of(
                                "width", 0.5F,
                                "color", WHITE
                        ), 13, WHITE,
                        new int[]{10, 0, 0, 0},
                        new int[]{0, 0, 0, 10}));
                profileCell.addElement(this.getItems(profile.interests(), "", new int[]{0, 0, 1, 0}));
                table.addCell(profileCell);

                final PdfPCell contentCell = new PdfPCell();
                contentCell.setBorder(NO_BORDER);
                contentCell.setBackgroundColor(WHITE);
                contentCell.setMinimumHeight(PageSize.A4.getHeight());

                final PdfPTable name = this.tableInTable(
                        this.contentInTable(
                                new Paragraph(
                                        String.format("%s%s".toUpperCase(), profile.firstName().charAt(0), profile.lastName().charAt(0)),
                                        new Font(HELVETICA, 34, Font.BOLD, BLACK)
                                ),
                                Map.of(),
                                new int[]{10, 0, 0, 0}

                        ),
                        new int[]{0, 0, 0, 15}
                );

                contentCell.addElement(name);
                final PdfPTable job = this.tableInTable(
                        this.contentInTable(
                                new Paragraph(
                                        profile.title(),
                                        new Font(HELVETICA, 16, Font.BOLD, GREEN)
                                ),
                                Map.of(),
                                new int[]{0, 0, 0, 0}

                        ),
                        new int[]{0, 0, 0, 10}
                );

                contentCell.addElement(job);

                final PdfPTable about = this.tableInTable(
                        this.contentInTable(
                                new Paragraph(
                                        profile.about(),
                                        new Font(HELVETICA, 10, NORMAL, BLACK)
                                ),
                                Map.of(),
                                new int[]{0, 0, 0, 0}

                        ),
                        new int[]{10, 15, 15, 10}
                );

                contentCell.addElement(about);

                contentCell.addElement(this.getTitle("Expériences", Map.of(
                                "width", 0.5F,
                                "color", GREEN
                        ), 18, BLACK,
                        new int[]{10, 0, 0, 0},
                        new int[]{0, 0, 0, 10}));

                final PdfPTable expreienceWrapperTable = new PdfPTable(1);
                expreienceWrapperTable.setWidthPercentage(100);
                expreienceWrapperTable.getDefaultCell().setBorder(NO_BORDER);

                final PdfPCell expreienceWrapperCell = new PdfPCell();
                expreienceWrapperCell.setBorder(0);
                expreienceWrapperCell.setPaddingRight(10);
                expreienceWrapperCell.setPaddingLeft(10);


                final Iterator<Experience> experienceIterator = profile.experiences().iterator();
                while (experienceIterator.hasNext()) {
                    final Experience item = experienceIterator.next();
                    final PdfPTable experienceTable = new PdfPTable(2);
                    experienceTable.getDefaultCell().setBorder(NO_BORDER);
                    experienceTable.setWidthPercentage(100);
                    experienceTable.setWidths(new int[]{1, 4});

                    final Paragraph dateParagrah = new Paragraph();
                    dateParagrah.add(new Chunk(String.format("%s\n%s", item.startDate(), item.endDate()), new Font(HELVETICA, 10, NORMAL, BLACK)));

                    final PdfPCell dateCell = new PdfPCell(dateParagrah);
                    dateCell.setBorder(0);
                    dateCell.setPaddingTop(11);
                    experienceTable.addCell(dateCell);

                    final PdfPCell experienceContentCell = new PdfPCell();
                    experienceContentCell.setBorder(0);
                    experienceContentCell.setPaddingBottom(4);
                    experienceContentCell.addElement(
                            this.getTitle(
                                    item.job(),
                                    Map.of(),
                                    13,
                                    BLACK,
                                    new int[]{0, 0, 0, 0},
                                    new int[]{0, 0, 4, 0}
                            )
                    );

                    item.tasks().forEach(task -> {
                        final Paragraph taskParagraph = new Paragraph();
                        taskParagraph.setFont(new Font(HELVETICA, 10, NORMAL, BLACK));
                        taskParagraph.add(String.format("- %s", task.title()));
                        experienceContentCell.addElement(taskParagraph);
                    });


                    experienceTable.addCell(experienceContentCell);
                    expreienceWrapperCell.addElement(experienceTable);
                    expreienceWrapperCell.addElement(NEWLINE);

                }

                expreienceWrapperTable.addCell(expreienceWrapperCell);
                contentCell.addElement(expreienceWrapperTable);
                table.addCell(contentCell);
                document.add(table);
                document.close();
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }

    private PdfPTable getTitle(final String title, final Map<String, Object> border, final int size, final BaseColor color, final int[] contentPaddings, final int[] wrapperPaddings) {
        return this.tableInTable(
                this.contentInTable(
                        new Paragraph(
                                title,
                                new Font(HELVETICA, size, Font.BOLD, color)
                        ),
                        border,
                        contentPaddings

                ),
                wrapperPaddings
        );
    }

    private PdfPTable getItems(final Set<Item> items, final String linePrefix, final int[] paddings) {
        final PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100);
        table.getDefaultCell().setBorder(NO_BORDER);

        final Iterator<Item> formationsIterator = items.iterator();
        while (formationsIterator.hasNext()) {
            final Item formationItem = formationsIterator.next();
            final PdfPCell itemCell = this.getLineAsPdfPCell(linePrefix, formationItem, paddings);
            table.addCell(itemCell);
        }
        return table;
    }

    private PdfPCell getLineAsPdfPCell(final String linePrefix, final Item item, final int[] paddings) {
        final Paragraph itemParagraph = new Paragraph();
        if (Strings.isNotEmpty(item.title())) {
            int style = NORMAL;
            if (Strings.isNotEmpty(item.subTitle()) || Strings.isNotEmpty(item.description())) {
                style = Font.BOLD;
            }
            itemParagraph.add(new Paragraph(String.format("%s%s", linePrefix, item.title()), new Font(HELVETICA, 10, style, WHITE)));
        }
        if (Strings.isNotEmpty(item.subTitle())) {
            itemParagraph.add(new Paragraph(String.format("%s%s", linePrefix, item.subTitle()), new Font(HELVETICA, 9, NORMAL, WHITE)));
        }
        if (Strings.isNotEmpty(item.description())) {
            itemParagraph.add(new Paragraph(String.format("%s%s", linePrefix, item.description()), new Font(HELVETICA, 9, NORMAL, WHITE)));
        }

        final PdfPTable itemTable = this.contentInTable(itemParagraph, Map.of(), paddings);
        final PdfPCell itemCell = new PdfPCell();
        itemCell.setPaddingBottom(0);
        itemCell.setPaddingLeft(10F);
        itemCell.setBorder(0);
        itemCell.addElement(itemTable);
        return itemCell;
    }

    private PdfPTable tableInTable(
            final PdfPTable content,
            final int[] paddings
    ) {

        final PdfPTable contentTable = new PdfPTable(1);
        contentTable.setWidthPercentage(100);
        contentTable.getDefaultCell().setBorder(NO_BORDER);

        final PdfPCell contentCell = new PdfPCell();
        contentCell.addElement(content);
        contentCell.setBorder(0);
        contentCell.setPaddingTop(paddings[0]);
        contentCell.setPaddingRight(paddings[1]);
        contentCell.setPaddingBottom(paddings[2]);
        contentCell.setPaddingLeft(paddings[3]);
        contentTable.addCell(contentCell);
        return contentTable;
    }

    private PdfPTable contentInTable(
            final Paragraph content,
            final Map<String, Object> border,
            final int[] paddings
    ) {
        final PdfPTable contentTable = new PdfPTable(1);
        contentTable.setWidthPercentage(100);
        contentTable.getDefaultCell().setBorder(NO_BORDER);

        final PdfPCell contentCell = new PdfPCell();
        contentCell.addElement(content);
        contentCell.setBorder(0);
        contentCell.setPaddingTop(paddings[0]);
        contentCell.setPaddingRight(paddings[1]);
        contentCell.setPaddingBottom(paddings[2]);
        contentCell.setPaddingLeft(paddings[3]);

        if (!border.isEmpty()) {
            contentCell.setPaddingBottom(4F);
            contentCell.setBorderColorBottom((BaseColor) border.get("color"));
            contentCell.setBorderWidthBottom((Float) border.get("width"));
        }
        contentTable.addCell(contentCell);
        return contentTable;
    }

    String capitalizeText(final String text) {
        return String.format(
                "%s%s",
                text.substring(0, 1).toUpperCase(),
                text.substring(1).toLowerCase()
        );
    }
}
