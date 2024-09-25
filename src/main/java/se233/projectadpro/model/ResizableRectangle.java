package se233.projectadpro.model;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class ResizableRectangle {
    private static final double HANDLE_SIZE = 8;
    private final Canvas canvas;
    private double x, y;
    private double width, height;
    private double startX, startY;
    private boolean resizing = false;
    private boolean moving = false;
    private int resizeEdge = -1;

    private enum Edge { NONE, TOP, RIGHT, BOTTOM, LEFT, TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT }

    public ResizableRectangle(double x, double y, double width, double height, Canvas canvas) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.canvas = canvas;

        // Set up mouse event handlers
        setupMouseEvents(this.canvas);
    }

    private void setupMouseEvents(Canvas canvas) {
        canvas.setOnMousePressed(this::handleMousePressed);
        canvas.setOnMouseDragged(this::handleMouseDragged);
        canvas.setOnMouseReleased(event -> handleMouseReleased());
    }

    private void handleMousePressed(MouseEvent e) {
        startX = e.getX();
        startY = e.getY();

        Edge edge = getEdge(startX, startY);
        if (edge == Edge.NONE) {
            if (startX >= x && startX <= x + width &&
                    startY >= y && startY <= y + height) {
                moving = true;
            }
        } else {
            resizing = true;
            resizeEdge = edge.ordinal();
        }
    }

    private void handleMouseDragged(MouseEvent e) {
        double dx = e.getX() - startX;
        double dy = e.getY() - startY;

        if (resizing) {
            resizeRectangle(dx, dy);
        } else if (moving) {
            moveRectangle(dx, dy);
        }

        startX = e.getX();
        startY = e.getY();

        drawRectangle();
    }

    private void handleMouseReleased() {
        resizing = false;
        moving = false;
    }

    public void resizeRectangle(double dx, double dy) {
        double canvasWidth = canvas.getWidth();
        double canvasHeight = canvas.getHeight();

        switch (Edge.values()[resizeEdge]) {
            case RIGHT:
                if (x + width + dx <= canvasWidth) {
                    width += dx;
                }
                break;
            case BOTTOM:
                if (y + height + dy <= canvasHeight) {
                    height += dy;
                }
                break;
            case LEFT:
                if (x + dx >= 0) {
                    x += dx;
                    width -= dx;
                }
                break;
            case TOP:
                if (y + dy >= 0) {
                    y += dy;
                    height -= dy;
                }
                break;
            case TOP_LEFT:
                if (x + dx >= 0 && y + dy >= 0) {
                    x += dx;
                    y += dy;
                    width -= dx;
                    height -= dy;
                }
                break;
            case TOP_RIGHT:
                if (y + dy >= 0 && x + width + dx <= canvasWidth) {
                    y += dy;
                    width += dx;
                    height -= dy;
                }
                break;
            case BOTTOM_LEFT:
                if (x + dx >= 0 && y + height + dy <= canvasHeight) {
                    x += dx;
                    width -= dx;
                    height += dy;
                }
                break;
            case BOTTOM_RIGHT:
                if (x + width + dx <= canvasWidth && y + height + dy <= canvasHeight) {
                    width += dx;
                    height += dy;
                }
                break;
            default:
                break;
        }
    }

    public void moveRectangle(double dx, double dy) {
        double newX = x + dx;
        double newY = y + dy;
        double canvasWidth = canvas.getWidth();
        double canvasHeight = canvas.getHeight();

        // Check if moving the rectangle stays within canvas bounds
        if (newX >= 0 && newX + width <= canvasWidth) {
            x = newX;
        }
        if (newY >= 0 && newY + height <= canvasHeight) {
            y = newY;
        }
    }

    public void drawRectangle() {
        GraphicsContext gc = this.canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());

        // Draw the rectangle
//        gc.setFill(Color.TRANSPARENT);
        gc.setFill(Color.rgb(173, 216, 230, 0.5));
        gc.fillRect(x, y, width, height);

        gc.setStroke(Color.BLUE);
        gc.setLineWidth(2.0);
        gc.strokeRect(x, y, width, height);

        // Draw the resize handles
        gc.setFill(Color.BLUE);
        drawHandles(gc);
    }

    private void drawHandles(GraphicsContext gc) {
        gc.fillRect(x - HANDLE_SIZE / 2, y - HANDLE_SIZE / 2, HANDLE_SIZE, HANDLE_SIZE); // Top-left
        gc.fillRect(x + width - HANDLE_SIZE / 2, y - HANDLE_SIZE / 2, HANDLE_SIZE, HANDLE_SIZE); // Top-right
        gc.fillRect(x - HANDLE_SIZE / 2, y + height - HANDLE_SIZE / 2, HANDLE_SIZE, HANDLE_SIZE); // Bottom-left
        gc.fillRect(x + width - HANDLE_SIZE / 2, y + height - HANDLE_SIZE / 2, HANDLE_SIZE, HANDLE_SIZE); // Bottom-right
        gc.fillRect(x + width / 2 - HANDLE_SIZE / 2, y - HANDLE_SIZE / 2, HANDLE_SIZE, HANDLE_SIZE); // Top-center
        gc.fillRect(x + width / 2 - HANDLE_SIZE / 2, y + height - HANDLE_SIZE / 2, HANDLE_SIZE, HANDLE_SIZE); // Bottom-center
        gc.fillRect(x - HANDLE_SIZE / 2, y + height / 2 - HANDLE_SIZE / 2, HANDLE_SIZE, HANDLE_SIZE); // Left-center
        gc.fillRect(x + width - HANDLE_SIZE / 2, y + height / 2 - HANDLE_SIZE / 2, HANDLE_SIZE, HANDLE_SIZE); // Right-center
    }

    private Edge getEdge(double x, double y) {
        boolean isLeft = x >= this.x - HANDLE_SIZE / 2 && x <= this.x + HANDLE_SIZE / 2;
        boolean isRight = x >= this.x + width - HANDLE_SIZE / 2 && x <= this.x + width + HANDLE_SIZE / 2;
        boolean isTop = y >= this.y - HANDLE_SIZE / 2 && y <= this.y + HANDLE_SIZE / 2;
        boolean isBottom = y >= this.y + height - HANDLE_SIZE / 2 && y <= this.y + height + HANDLE_SIZE / 2;
        boolean isCenterX = x >= this.x + width / 2 - HANDLE_SIZE / 2 && x <= this.x + width / 2 + HANDLE_SIZE / 2;
        boolean isCenterY = y >= this.y + height / 2 - HANDLE_SIZE / 2 && y <= this.y + height / 2 + HANDLE_SIZE / 2;

        if (isLeft && isTop) return Edge.TOP_LEFT;
        if (isRight && isTop) return Edge.TOP_RIGHT;
        if (isLeft && isBottom) return Edge.BOTTOM_LEFT;
        if (isRight && isBottom) return Edge.BOTTOM_RIGHT;

        if (isCenterX && isTop) return Edge.TOP;
        if (isCenterX && isBottom) return Edge.BOTTOM;
        if (isCenterY && isLeft) return Edge.LEFT;
        if (isCenterY && isRight) return Edge.RIGHT;

        return Edge.NONE;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public void setHeight(double height) {
        this.height = height;
    }
}