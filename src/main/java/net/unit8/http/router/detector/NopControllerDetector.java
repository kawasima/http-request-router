package net.unit8.http.router.detector;

import net.unit8.http.router.ControllerDetector;

import java.util.Collections;
import java.util.List;

public class NopControllerDetector implements ControllerDetector{
    public List<String> detect() {
        return Collections.EMPTY_LIST;
    }
}
