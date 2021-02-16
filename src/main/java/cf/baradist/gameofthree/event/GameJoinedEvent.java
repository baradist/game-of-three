package cf.baradist.gameofthree.event;

import cf.baradist.gameofthree.dto.ResultDto;

public class GameJoinedEvent extends AbstractResultEvent {
    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source    the object on which the event initially occurred or with
     *                  which the event is associated (never {@code null})
     * @param resultDto
     */
    public GameJoinedEvent(Object source, ResultDto resultDto) {
        super(source, resultDto);
    }
}
