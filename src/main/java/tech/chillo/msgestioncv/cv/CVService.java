package tech.chillo.msgestioncv.cv;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tech.chillo.msgestioncv.pdf.PDFEditorService;
import tech.chillo.msgestioncv.pdf.PdfService;
import tech.chillo.msgestioncv.profile.Profile;
import tech.chillo.msgestioncv.profile.ProfileService;

import java.util.Set;

@Service
@AllArgsConstructor
public class CVService {
    ProfileService profileService;
    PdfService pdfService;
    PDFEditorService pdfEditorService;

    public void generate() {
        final Set<Profile> profiles = this.profileService.readFromFiles();
        this.pdfEditorService.generateCV(profiles);
    }
}
