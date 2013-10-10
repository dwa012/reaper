class InvoiceLinesController < ApplicationController
  # GET /invoice_lines
  # GET /invoice_lines.json
  def index
    @invoice_lines = InvoiceLine.all

    respond_to do |format|
      format.html # index.html.erb
      format.json { render json: @invoice_lines }
    end
  end

  # GET /invoice_lines/1
  # GET /invoice_lines/1.json
  def show
    @invoice_line = InvoiceLine.find(params[:id])

    respond_to do |format|
      format.html # show.html.erb
      format.json { render json: @invoice_line }
    end
  end

  # GET /invoice_lines/new
  # GET /invoice_lines/new.json
  def new
    @invoice_line = InvoiceLine.new

    respond_to do |format|
      format.html # new.html.erb
      format.json { render json: @invoice_line }
    end
  end

  # GET /invoice_lines/1/edit
  def edit
    @invoice_line = InvoiceLine.find(params[:id])
  end

  # POST /invoice_lines
  # POST /invoice_lines.json
  def create
    @invoice_line = InvoiceLine.new(params[:invoice_line])

    respond_to do |format|
      if @invoice_line.save
        format.html { redirect_to @invoice_line, notice: 'Invoice line was successfully created.' }
        format.json { render json: @invoice_line, status: :created, location: @invoice_line }
      else
        format.html { render action: "new" }
        format.json { render json: @invoice_line.errors, status: :unprocessable_entity }
      end
    end
  end

  # PUT /invoice_lines/1
  # PUT /invoice_lines/1.json
  def update
    @invoice_line = InvoiceLine.find(params[:id])

    respond_to do |format|
      if @invoice_line.update_attributes(params[:invoice_line])
        format.html { redirect_to @invoice_line, notice: 'Invoice line was successfully updated.' }
        format.json { head :no_content }
      else
        format.html { render action: "edit" }
        format.json { render json: @invoice_line.errors, status: :unprocessable_entity }
      end
    end
  end

  # DELETE /invoice_lines/1
  # DELETE /invoice_lines/1.json
  def destroy
    @invoice_line = InvoiceLine.find(params[:id])
    @invoice_line.destroy

    respond_to do |format|
      format.html { redirect_to invoice_lines_url }
      format.json { head :no_content }
    end
  end
end
