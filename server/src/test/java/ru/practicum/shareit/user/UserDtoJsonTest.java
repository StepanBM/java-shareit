package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class UserDtoJsonTest {

    private final JacksonTester<UserDto> jsonUserDto;
    private final JacksonTester<NewUserRequest> jsonNewUserDto;
    private final JacksonTester<UpdateUserRequest> jsonUpdateUserDto;

    @Autowired
    public UserDtoJsonTest(
            JacksonTester<UserDto> jsonUserDto,
            JacksonTester<NewUserRequest> jsonNewUserDto,
            JacksonTester<UpdateUserRequest> jsonUpdateUserDto) {
        this.jsonUserDto = jsonUserDto;
        this.jsonNewUserDto = jsonNewUserDto;
        this.jsonUpdateUserDto = jsonUpdateUserDto;
    }

    @Test
    public void serializeUserDtoJsonTest() throws Exception {

        UserDto usertDto = new UserDto();
        usertDto.setId(1L);
        usertDto.setName("Sergey");
        usertDto.setEmail("ser007@mail.com");

        JsonContent<UserDto> json = jsonUserDto.write(usertDto);

        assertThat(json).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(json).extractingJsonPathStringValue("$.name").isEqualTo("Sergey");
        assertThat(json).extractingJsonPathStringValue("$.email").isEqualTo("ser007@mail.com");
    }

    @Test
    public void serializeNewUserDtoJsonTest() throws Exception {

        NewUserRequest newUserRequest = new NewUserRequest();
        newUserRequest.setName("Sergey");
        newUserRequest.setEmail("ser007@mail.com");

        JsonContent<NewUserRequest> json = jsonNewUserDto.write(newUserRequest);

        assertThat(json).extractingJsonPathStringValue("$.name").isEqualTo("Sergey");
        assertThat(json).extractingJsonPathStringValue("$.email").isEqualTo("ser007@mail.com");
    }

    @Test
    public void serializeUpdateUserDtoJsonTest() throws Exception {

        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setName("Sergey");
        updateUserRequest.setEmail("ser007@mail.com");

        JsonContent<UpdateUserRequest> json = jsonUpdateUserDto.write(updateUserRequest);

        assertThat(json).extractingJsonPathStringValue("$.name").isEqualTo("Sergey");
        assertThat(json).extractingJsonPathStringValue("$.email").isEqualTo("ser007@mail.com");
    }
}
