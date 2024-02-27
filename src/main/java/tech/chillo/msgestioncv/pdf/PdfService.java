package tech.chillo.msgestioncv.pdf;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
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
import java.util.Set;
import java.util.stream.Collectors;

import static com.itextpdf.text.Chunk.NEWLINE;
import static com.itextpdf.text.Element.ALIGN_CENTER;

@Service
public class PdfService {
    public static final String DEST = "./target/sandbox/merge/make_a3_booklet.pdf";

    public void generateCV(final Set<Profile> profiles) {
        profiles.stream().peek(profile -> {
            try {
                final Document document = new Document();
                document.setMargins(0, 0, 0, 0);
                final PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(profile.firstName().toLowerCase() + ".pdf"));
                final PdfDocument pdfDoc = new PdfDocument();
                pdfDoc.addWriter(writer);
                pdfDoc.setPageSize(PageSize.A4);

                document.open();

                final PdfPTable table = new PdfPTable(2);
                table.setWidthPercentage(100);
                table.setWidths(new int[]{1, 3});
                table.getDefaultCell().setBorder(Rectangle.NO_BORDER);

                final PdfPCell profileCell = new PdfPCell();
                profileCell.setBorder(Rectangle.NO_BORDER);
                profileCell.setBackgroundColor(new BaseColor(29, 58, 138));
                profileCell.setMinimumHeight(PageSize.A4.getHeight());
                final Paragraph initials = new Paragraph(
                        String.format("%s%s".toUpperCase(), profile.firstName().charAt(0), profile.lastName().charAt(0)),
                        new Font(Font.FontFamily.HELVETICA, 40, Font.BOLD, BaseColor.WHITE)
                );
                initials.setAlignment(ALIGN_CENTER);
                profileCell.addElement(this.contentInTable(initials, false, 40, 40, 15, new BaseColor(7, 6, 6)));

                profileCell.addElement(this.getProfileCellItem("Formations", profile.formations(), ""));
                profileCell.addElement(NEWLINE);
                profileCell.addElement(this.getProfileCellItem("Compétences", profile.skills(), "- "));
                profileCell.addElement(NEWLINE);
                profileCell.addElement(this.getProfileCellItem("Langues", profile.languages(), ""));
                profileCell.addElement(this.getProfileCellItem("Intérêts", profile.interests(), ""));

                table.addCell(profileCell);


                final PdfPCell contentCell = new PdfPCell();
                contentCell.setBorder(Rectangle.NO_BORDER);
                contentCell.setBackgroundColor(BaseColor.WHITE);
                contentCell.setMinimumHeight(PageSize.A4.getHeight());

                final Paragraph name = new Paragraph(
                        String.format("%s%s".toUpperCase(), profile.firstName().charAt(0), profile.lastName().charAt(0)),
                        new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD, new BaseColor(7, 6, 6))
                );
                contentCell.addElement(this.contentInTable(name, false, 0, 0, 15, new BaseColor(7, 6, 6)));

                final Paragraph job = new Paragraph(
                        profile.title(),
                        new Font(Font.FontFamily.HELVETICA, 14, Font.NORMAL, new BaseColor(7, 6, 6))
                );
                contentCell.addElement(this.contentInTable(job, false, 0, 0, 15, new BaseColor(7, 6, 6)));


                final Paragraph about = new Paragraph(
                        profile.about(),
                        new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, new BaseColor(7, 6, 6))
                );
                contentCell.addElement(this.contentInTable(about, false, 0, 20, 15, new BaseColor(7, 6, 6)));


                contentCell.addElement(this.getExperiencesCellItem("Expériences", profile.experiences(), "", new BaseColor(7, 6, 6)));
                contentCell.addElement(NEWLINE);

                table.addCell(contentCell);

                document.add(table);

                document.close();
                //FileUtils.writeByteArrayToFile(new File("test.pdf"), out.toByteArray());
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }).collect(Collectors.toSet());


    }

    private Element getProfileCellItem(final String title, final Set<Item> items, final String linePrefix) {
        final PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100);
        table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
        final Font boldWhiteText = new Font(Font.FontFamily.HELVETICA, 13, Font.BOLD, BaseColor.WHITE);

        final Paragraph titleParagraph = new Paragraph();
        titleParagraph.setFont(boldWhiteText);
        titleParagraph.add(title);

        final PdfPCell titleCell = new PdfPCell();
        titleCell.setBorder(0);
        titleCell.setPaddingLeft(10F);
        titleCell.setPaddingBottom(0);
        titleCell.setPaddingRight(0);

        final PdfPTable contentTable = this.contentInTable(titleParagraph, true, 0, 0, 0, BaseColor.WHITE);

        titleCell.addElement(contentTable);
        table.addCell(titleCell);

        items.forEach(item -> {

            final PdfPCell itemCell = this.getLineAsPdfPCell(linePrefix, item);
            table.addCell(itemCell);
        });

        return table;
    }

    private PdfPCell getLineAsPdfPCell(final String linePrefix, final Item item) {
        final Paragraph itemParagraph = new Paragraph();
        if (Strings.isNotEmpty(item.title())) {
            int style = Font.NORMAL;
            if (Strings.isNotEmpty(item.subTitle()) || Strings.isNotEmpty(item.description())) {
                style = Font.BOLD;
            }
            itemParagraph.add(new Paragraph(String.format("%s%s", linePrefix, item.title()), new Font(Font.FontFamily.HELVETICA, 10, style, BaseColor.WHITE)));
        }
        if (Strings.isNotEmpty(item.subTitle())) {
            itemParagraph.add(new Paragraph(String.format("%s%s", linePrefix, item.subTitle()), new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL, BaseColor.WHITE)));
        }
        if (Strings.isNotEmpty(item.description())) {
            itemParagraph.add(new Paragraph(String.format("%s%s", linePrefix, item.description()), new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL, BaseColor.WHITE)));
        }

        final PdfPTable itemTable = this.contentInTable(itemParagraph, false, 0, 10, 0, BaseColor.WHITE);
        final PdfPCell itemCell = new PdfPCell();

        itemCell.setPaddingBottom(0);
        itemCell.setPaddingLeft(10F);
        itemCell.setBorder(0);
        itemCell.addElement(itemTable);
        return itemCell;
    }

    private Element getExperiencesCellItem(final String title, final Set<Experience> items, final String linePrefix, final BaseColor color) {
        final PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100);
        table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
        final Font boldWhiteText = new Font(Font.FontFamily.HELVETICA, 13, Font.BOLD, color);

        final Paragraph titleParagraph = new Paragraph();
        titleParagraph.setFont(boldWhiteText);
        titleParagraph.add(title);

        final PdfPCell titleCell = new PdfPCell();
        titleCell.setBorder(0);
        titleCell.setPaddingLeft(20);
        titleCell.setPaddingBottom(5);
        titleCell.setPaddingRight(20);

        final PdfPTable contentTable = this.contentInTable(titleParagraph, true, 20, 0, 0, color);

        titleCell.addElement(contentTable);
        table.addCell(titleCell);
        items.forEach(item -> {

            final PdfPTable itemTable = new PdfPTable(2);
            itemTable.setWidthPercentage(100);
            try {
                itemTable.setWidths(new int[]{1, 4});
                itemTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);

                final PdfPCell dateCell = new PdfPCell();
                dateCell.setBorder(0);
                dateCell.setPadding(0);
                final Paragraph dateParagrah = new Paragraph();
                dateParagrah.add(new Chunk(String.format("%s\n%s", item.startDate(), item.endDate()), new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, color)));

                dateCell.addElement(this.contentInTable(dateParagrah, false, 15, 0, 0, color));
                itemTable.addCell(dateCell);

                final PdfPCell experienceCell = new PdfPCell();
                experienceCell.setPaddingRight(10);
                experienceCell.setBorder(0);

                final Paragraph experienceTitleParagraph = new Paragraph();
                experienceTitleParagraph.setFont(new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, color));
                experienceTitleParagraph.add(item.job());

                experienceCell.addElement(this.contentInTable(experienceTitleParagraph, false, 15, 0, 0, color));

                final Paragraph companyParagraph = new Paragraph();
                companyParagraph.setFont(new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, color));
                companyParagraph.add(String.format("%s / %s", item.company(), item.location()));
                experienceCell.addElement(this.contentInTable(companyParagraph, false, 5, 0, 0, color));

                item.tasks().forEach(task -> {
                    final Paragraph taskParagraph = new Paragraph();
                    taskParagraph.setFont(new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, color));
                    taskParagraph.add(String.format("%s", task.title()));
                    experienceCell.addElement(this.contentInTable(taskParagraph, false, 5, 0, 0, color));
                });

                itemTable.addCell(experienceCell);

                final PdfPCell itemCell = new PdfPCell();
                itemCell.setPaddingBottom(0);
                itemCell.setPaddingLeft(20F);
                itemCell.setBorder(0);
                itemCell.addElement(itemTable);

                table.addCell(itemCell);
            } catch (final DocumentException e) {
                throw new RuntimeException(e);
            }

        });

        return table;
    }

    private PdfPTable contentInTable(
            final Paragraph content,
            final boolean border,
            final float paddingTop,
            final float paddingBottom,
            final float paddingLeft,
            final BaseColor color
    ) {
        final PdfPTable contentTable = new PdfPTable(1);
        contentTable.setWidthPercentage(100);
        contentTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);
        final PdfPCell contentCell = new PdfPCell();
        contentCell.addElement(content);
        contentCell.setBorder(0);
        contentCell.setPaddingLeft(paddingLeft);
        contentCell.setPaddingTop(paddingTop);
        contentCell.setPaddingBottom(paddingBottom);
        if (border) {
            contentCell.setPaddingBottom(5F);
            contentCell.setBorderColorBottom(color);
            contentCell.setBorderWidthBottom(0.5F);
        }
        contentTable.addCell(contentCell);
        return contentTable;
    }
}
