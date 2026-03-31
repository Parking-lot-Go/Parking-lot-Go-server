package car.demo.domain.supportticket.dto;

import car.demo.domain.supportticket.entity.SupportTicketCategory;
import car.demo.domain.supportticket.entity.SupportTicketType;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SupportTicketRequestValidationTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        validatorFactory.close();
    }

    @Test
    @DisplayName("create request requires non blank title")
    void createRequestRequiresTitle() {
        // given
        SupportTicketRequest.Create request = new SupportTicketRequest.Create(
                SupportTicketType.INQUIRY,
                SupportTicketCategory.INQUIRY_ACCOUNT,
                " ",
                "content",
                "context",
                "reply@example.com",
                "1.0.0",
                "Android 14",
                "Pixel 8"
        );

        // when
        var violations = validator.validate(request);

        // then
        assertThat(violations).extracting("message")
                .contains("title must not be blank");
    }

    @Test
    @DisplayName("update request allows null but rejects blank values")
    void updateRequestRejectsBlankValues() {
        // given
        SupportTicketRequest.Update request = new SupportTicketRequest.Update(
                null,
                null,
                " ",
                null,
                " ",
                null,
                null,
                null,
                null
        );

        // when
        var violations = validator.validate(request);

        // then
        assertThat(violations).extracting("message")
                .contains("title must not be blank", "extraContent1 must not be blank");
    }

    @Test
    @DisplayName("status change request requires status")
    void changeStatusRequestRequiresStatus() {
        // given
        SupportTicketRequest.ChangeStatus request = new SupportTicketRequest.ChangeStatus(
                null,
                "memo"
        );

        // when
        var violations = validator.validate(request);

        // then
        assertThat(violations).extracting("message")
                .contains("status is required");
    }
}
