package cf.baradist.gameofthree.event;

import cf.baradist.gameofthree.dto.ResultDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MoveResultEvent extends AbstractResultEvent {
    private List<String> receivers;

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source    the object on which the event initially occurred or with
     *                  which the event is associated (never {@code null})
     * @param resultDto
     */
    public MoveResultEvent(Object source, ResultDto resultDto) {
        super(source, resultDto);
    }

    public MoveResultEvent(Object source, ResultDto resultDto, List<String> receivers) {
        super(source, resultDto);
        this.receivers = receivers;
    }
}
