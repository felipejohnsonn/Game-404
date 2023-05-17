package game.gui;

import game.framework.GameTime;
import game.framework.Rectangle;
import game.input.Mouse;
import game.input.MouseKeys;

import java.awt.*;
import java.util.LinkedList;

public class Menu implements GuiComponent
{
    // Propriedades da classe
    public final String LABEL;
    private MenuState menuState;
    private LinkedList<MenuItem> items;
    // Valores que precisam ser definidos pelo pai
    private Anchor anchor;
    private Font font;
    private Color paneColor, borderColor, fontColor;
    private Rectangle closedBoundingBox, openBoundingBox;

    public Menu(String label, Color fontColor)
    {
        this.LABEL = label;
        this.fontColor = fontColor;
        this.menuState = MenuState.CLOSED;
        this.items = new LinkedList<>();
    }

    public Menu(String label)
    {
        this(label, Color.WHITE);
    }

    public void add(MenuItem menuItem)
    {
        items.add(menuItem);
    }

    protected void setColors(Color paneColor, Color borderColor)
    {
        this.paneColor = paneColor;
        this.borderColor = borderColor;
    }

    protected void setProperties(Anchor anchor, int x, int y, int closedWidth, int closedHeight)
    {
        this.anchor = anchor;
        this.closedBoundingBox = new Rectangle(x, y, closedWidth, closedHeight);
        // TODO: precisa corrigir a constante de largura 200
        int openHeight = (closedHeight * items.size()) + items.size();
        switch(anchor)
        {
            default:
            case TOP:
                this.openBoundingBox = new Rectangle
                (
                    closedBoundingBox.x,
                    closedBoundingBox.y + closedBoundingBox.height,
                    200,
                    openHeight
                );
                break;
            case BOTTOM:
                this.openBoundingBox = new Rectangle
                (
                    closedBoundingBox.x,
                    closedBoundingBox.y - openHeight,
                    200,
                    openHeight
                );
                break;
        }
    }


    public int getClosedWidth()
    {
        return closedBoundingBox.width;
    }

    protected void setFont(Font font)
    {
        this.font = font;
    }

    @Override
    public void initialize()
    {
        int yOffset;
        for(int i = 0; i < items.size(); ++i)
        {
            yOffset = i * closedBoundingBox.height;
            items.get(i).setProperties
            (
                anchor,
                openBoundingBox.x,
                openBoundingBox.y + yOffset + (i * 1),
                // TODO: FIX 200 largura para o menu.
                200,
                closedBoundingBox.height
            );
            items.get(i).setFont(font);
            items.get(i).setFontColor(fontColor);
            items.get(i).initialize();
        }
    }

    @Override
    public void loadContent(){}

    @Override
    public void update(GameTime gameTime)
    {
        // Se o Menu tiver algum item, verifique se alguém está clicando nele.
        if(items.size() > 0)
        {
            if(closedBoundingBox.intersects(Mouse.getPosition()) && Mouse.buttonDownOnce(MouseKeys.BUTTON_1))
                menuState = MenuState.OPEN;
            if(menuState == MenuState.OPEN && !closedBoundingBox.intersects(Mouse.getPosition()) && !openBoundingBox.intersects(Mouse.getPosition()))
                menuState = MenuState.CLOSED;
        }

        switch(menuState)
        {
            case OPEN:
                for(MenuItem item : items)
                    item.update(gameTime);
                break;
        }
    }
    @Override
    public void draw(Graphics2D g2d)
    {
        g2d.setFont(font);
        g2d.setColor(fontColor);

        g2d.drawString(LABEL, closedBoundingBox.x + 10, closedBoundingBox.y + 15);

        if(items.size() > 0 && menuState == MenuState.OPEN)
        {
            g2d.setColor(paneColor);

            g2d.fillRect(openBoundingBox.x, openBoundingBox.y, openBoundingBox.width, openBoundingBox.height);

            for(MenuItem item : items)
                item.draw(g2d);

            g2d.setColor(borderColor);
            g2d.drawRect(openBoundingBox.x, openBoundingBox.y, openBoundingBox.width, openBoundingBox.height);
        }
    }
}