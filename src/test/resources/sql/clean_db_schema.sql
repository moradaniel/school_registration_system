

ALTER SEQUENCE hibernate_sequence RESTART WITH 1;


TRUNCATE TABLE student
    RESTART IDENTITY CASCADE;

TRUNCATE TABLE course
    RESTART IDENTITY CASCADE;

TRUNCATE TABLE course_enrollment
    RESTART IDENTITY CASCADE;



