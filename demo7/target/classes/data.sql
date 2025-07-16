-- USERS tablosuna örnek veri ekleme
INSERT INTO USERS (FIRST_NAME, LAST_NAME, LOGIN, PASSWORD, AGE, ROLE) VALUES
                                                                          ('Ahmet', 'Yılmaz', 'ayilmaz', 'hashed_password1', 30, 'ROLE_ADMIN'),
                                                                          ('Mehmet', 'Demir', 'mdemir', 'hashed_password2', 25, 'ROLE_FULL_USER'),
                                                                          ('Ayşe', 'Kara', 'akara', 'hashed_password3', 28, 'ROLE_LIMITED_USER');

INSERT INTO CATEGORY (NAME)
SELECT 'technology' WHERE NOT EXISTS (SELECT 1 FROM CATEGORY WHERE NAME = 'technology');
INSERT INTO CATEGORY (NAME)
SELECT 'science' WHERE NOT EXISTS (SELECT 1 FROM CATEGORY WHERE NAME = 'science');
INSERT INTO CATEGORY (NAME)
SELECT 'arts' WHERE NOT EXISTS (SELECT 1 FROM CATEGORY WHERE NAME = 'arts');

-- INFORMATION tablosuna örnek veri ekleme
INSERT INTO INFORMATION (TITLE, CONTENT, LINK, DATE_ADDED, OWNER_ID, CATEGORY_ID) VALUES
                                                                                      ('Java Basics', 'Java hakkında temel bilgiler.', 'http://example.com/java-basics', '2025-06-01', 2, 1),
                                                                                      ('Physics Intro', 'Fizik konularına giriş.', null, '2025-06-01', 3, 2),
                                                                                      ('Modern Art', 'Modern sanat akımları üzerine.', 'http://example.com/modern-art', '2025-06-01', 1, 3);
