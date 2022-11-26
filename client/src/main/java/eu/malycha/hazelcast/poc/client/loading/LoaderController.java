package eu.malycha.hazelcast.poc.client.loading;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/load")
public class LoaderController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoaderController.class);

    private final LoaderService service;

    public LoaderController(LoaderService service) {
        this.service = service;
    }

    @PostMapping("/pojo/{number}")
    @ResponseStatus(HttpStatus.CREATED)
    public void load_pojo(int number) {
        service.load_pojo(number);
    }

    @PostMapping("/protobuf/{number}")
    @ResponseStatus(HttpStatus.CREATED)
    public void load_protobuf(int number) {
        service.load_protobuf(number);
    }
}
