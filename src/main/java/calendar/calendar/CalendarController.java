package calendar.calendar;

import biweekly.Biweekly;
import biweekly.ICalVersion;
import biweekly.ICalendar;
import biweekly.component.VEvent;
import biweekly.io.text.ICalWriter;
import biweekly.property.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import static biweekly.property.Status.*;

@RestController
@RequestMapping("/calendar")
public class CalendarController {

    @GetMapping("calendar/generate/currentMonth")
    ResponseEntity<Resource> calendar() throws IOException {

        Document weeiaCalendar = Jsoup.connect("http://www.weeia.p.lodz.pl/").get();
        Elements events = weeiaCalendar.select("a.active");


        ICalendar ical = new ICalendar();
        ical.setMethod("PUBLISH");
        ical.setProductId("weeia");
        ical.setCalendarScale(CalendarScale.gregorian());
        ical.setVersion(ICalVersion.V2_0);
        events.eachText()
                .forEach(day -> {
                    ical.addEvent(generateEvent(Integer.parseInt(day)));
                } );


        File file = new File("calendar.ics");
        Biweekly.write(ical).go(file);


        InputStreamResource resource = new InputStreamResource(new FileInputStream("calendar.ics"));
        return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);
    }

    private VEvent generateEvent(int day) {
        VEvent vEvent = new VEvent();
        vEvent.setSummary("heyy");
        vEvent.setCreated(new Date(2019, Calendar.NOVEMBER, 1));
        vEvent.setDateTimeStamp(new Date());
        vEvent.setUid(Uid.random());
        vEvent.setSequence(0);
        vEvent.setTransparency(Transparency.opaque());
        vEvent.setDateStart(new Date(2019, Calendar.NOVEMBER, day));
        vEvent.setDateEnd(new DateEnd(new Date(2019, Calendar.NOVEMBER, ++day)));
        vEvent.setStatus(confirmed());

        return vEvent;
    }
}
