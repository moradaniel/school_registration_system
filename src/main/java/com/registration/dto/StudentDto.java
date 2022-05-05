package com.registration.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.registration.model.Student;
import com.registration.model.StudentId;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.validation.constraints.NotBlank;
import java.util.Objects;

public class StudentDto {

	@NotBlank
	private String firstName;
	@NotBlank
	private String lastName;
	@NotBlank
	private String studentId;

	@JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
	public StudentDto(@JsonProperty("firstName")String firstName,
					  @JsonProperty("lastName")String lastName,
					  @JsonProperty("studentId")String studentId) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.studentId = studentId;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getStudentId() {
		return studentId;
	}

	public static StudentDto of(Student student) {
		return new StudentDto(student.getFirstName(),
				student.getLastName(),
				student.getStudentId().asString()
				);
	}

	public static Student of(StudentDto studentDto) {
		return new Student(studentDto.getFirstName(),
				studentDto.getLastName(),
				new StudentId(studentDto.getStudentId()));
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("firstName", firstName)
				.append("lastName", lastName)
				.append("studentId", studentId)
				.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		StudentDto that = (StudentDto) o;
		return Objects.equals(firstName, that.firstName) && Objects.equals(lastName, that.lastName) && Objects.equals(studentId, that.studentId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(firstName, lastName, studentId);
	}
}
