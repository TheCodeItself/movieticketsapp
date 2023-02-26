package com.larasierra.movietickets.moviemedia.model;

import java.io.InputStream;

public record GetFileResponse(
    InputStream file,
    String filename,
    String contentType,
    Long fileLength
) {}
