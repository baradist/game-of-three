package cf.baradist.gameofthree.event;

import cf.baradist.gameofthree.dto.ResultDto;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class AbstractResultEvent extends ApplicationEvent {
    private final ResultDto resultDto;

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source    the object on which the event initially occurred or with
     *                  which the event is associated (never {@code null})
     * @param resultDto
     */
    public AbstractResultEvent(Object source, ResultDto resultDto) {
        super(source);
        this.resultDto = resultDto;
    }
}
