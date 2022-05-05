package com.registration.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.registration.model.Course;
import com.registration.model.CourseId;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.validation.constraints.NotBlank;
import java.util.Objects;


public class CourseDto {

	@NotBlank
	private String name;

	@NotBlank
	private String courseId;

	@JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
	public CourseDto(@JsonProperty("name")String name,
                     @JsonProperty("courseId")String courseId) {
		this.name = name;
		this.courseId = courseId;
	}

	public String getName() {
		return name;
	}

	public String getCourseId() {
		return courseId;
	}

	public static CourseDto of(Course course) {
		return new CourseDto(course.getName(),
				course.getCourseId().asString());
	}

	public static Course of(CourseDto courseDto) {
		return new Course(courseDto.getName(),
				new CourseId(courseDto.getCourseId()));
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("name", name)
				.append("courseId", courseId)
				.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		CourseDto courseDto = (CourseDto) o;
		return Objects.equals(name, courseDto.name) && Objects.equals(courseId, courseDto.courseId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, courseId);
	}
}
