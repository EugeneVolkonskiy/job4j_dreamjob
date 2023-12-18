package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.service.FileService;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FileControllerTest {

    private FileService fileService;
    private FileController fileController;

    @BeforeEach
    public void initServices() {
        fileService = mock(FileService.class);
        fileController = new FileController(fileService);
    }

    @Test
    public void whenFileGetByIdSuccessfully() {
        var testFile = new FileDto("testFile.img", new byte[]{1, 2, 3});
        when(fileService.getFileById(anyInt())).thenReturn(Optional.of(testFile));
        var result = fileController.getById(anyInt());
        assertThat(result).isEqualTo(ResponseEntity.ok(testFile.getContent()));
    }

    @Test
    public void whenFileNotFoundById() {
        when(fileService.getFileById(anyInt())).thenReturn(Optional.empty());
        var result = fileController.getById(anyInt());
        assertThat(result).isEqualTo(ResponseEntity.notFound().build());
    }
}