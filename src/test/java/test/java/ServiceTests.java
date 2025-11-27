package test.java;

import edu.univ.erp.data.EnrollmentStore;
import edu.univ.erp.data.SectionStore;
import edu.univ.erp.data.SettingsStore;
import edu.univ.erp.domain.Section;
import edu.univ.erp.service.EnrollmentService;
import edu.univ.erp.service.StudentService;
import edu.univ.erp.access.AccessControl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;


import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ServiceTests {

    @Mock EnrollmentStore EnrollmentStore;
    @Mock SectionStore SectionStore;
    @Mock AccessControl accessControl;
    @Mock SettingsStore SettingsStore;
    @Mock EnrollmentService EnrollmentService;
    @Mock SectionStore sectionStore;


    @InjectMocks StudentService studentService; // adapt name if different

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void register_success_whenSeatAvailable_and_notDuplicate_and_notMaintenance() {
        int studentId = 3;
        int sectionId = 1;
        Section section = new Section(sectionId, 1, 2); // (id, code, capacity)
        when(SettingsStore.getSetting("maintenance_on")).thenReturn(("false"));
        when(SettingsStore.getSetting("add_drop_period_on")).thenReturn(("true"));
        when(SectionStore.findById(sectionId)).thenReturn((section));
        when(EnrollmentStore.exists(studentId, sectionId)).thenReturn(false);

        var result = EnrollmentService.registerForSection(studentId);

        assertThat(result ==null).isTrue();
    }

    @Test
    void register_blocked_if_duplicate() {
        int studentId = 4;
        int sectionId = 2;
        when(SettingsStore.getSetting("maintenance_on")).thenReturn("false");
        when(SettingsStore.getSetting("add_drop_period_on")).thenReturn(("true"));
        when(EnrollmentStore.exists(studentId, sectionId)).thenReturn(true);

        var result = EnrollmentService.registerForSection(studentId);

        assertThat(result!=null);
        verify(EnrollmentStore, never()).create(any());
    }

    @Test
    void register_blocked_if_full() {
        int studentId = 4;
        int sectionId = 2;
        Section full = new Section(sectionId, 2, 30);

        when(SettingsStore.getSetting("add_drop_period_on")).thenReturn(("true"));
        when(EnrollmentStore.exists(studentId, sectionId)).thenReturn(false);
        when(sectionStore.findById(sectionId)).thenReturn(full);

        var result = EnrollmentService.registerForSection(studentId);

        assertThat(result!=null);
        verify(EnrollmentStore, never()).create(any());
    }

    @Test
    void register_blocked_if_maintenance_on() {
        int studentId = 3;
        int sectionId = 4;
        when(SettingsStore.getSetting("maintenance_on")).thenReturn("true");
        var res = EnrollmentService.registerForSection(studentId);
        assertThat(res != null);

    }
//
    @Test
    void drop_success_before_deadline() {
        int studentId = 3;
        int sectionId = 4;
        when(SettingsStore.getSetting("add_drop_period_on")).thenReturn(("true"));
        var res = EnrollmentService.registerForSection(studentId);
        assertThat(res == null);

    }

    @Test
    void drop_blocked_after_deadline() {
        int studentId = 3;
        int sectionId = 4;
        when(SettingsStore.getSetting("add_drop_period_on")).thenReturn(("false"));
        var res = EnrollmentService.registerForSection(studentId);
        assertThat(res != null);
    }
}
