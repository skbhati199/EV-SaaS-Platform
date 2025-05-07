#!/bin/bash

# EV SaaS Platform Task Manager Script
# This script provides a simple CLI interface to interact with the task management system

REQUEST_ID="req-1"

show_help() {
  echo "EV SaaS Platform Task Manager"
  echo ""
  echo "Usage: ./task_manager.sh [command]"
  echo ""
  echo "Commands:"
  echo "  list         List all tasks and their status"
  echo "  next         Get the next task to work on"
  echo "  done [id]    Mark a task as done (e.g., done task-1)"
  echo "  approve [id] Approve a completed task (e.g., approve task-1)"
  echo "  help         Show this help message"
  echo ""
  echo "Example: ./task_manager.sh next"
}

list_tasks() {
  echo "Listing all tasks for the EV SaaS Platform project..."
  echo "Request ID: $REQUEST_ID"
  echo ""
  echo "Phase 1: Core Platform"
  echo "- [task-1] Set up mono repo with TurboRepo"
  echo "- [task-2] Setup PostgreSQL DB and schema migration"
  echo "- [task-3] Implement Auth + EVSE registration"
  echo "- [task-4] Integrate OCPP 1.6 backend"
  echo "- [task-5] Basic Admin UI"
  echo ""
  echo "Phase 2: Protocol & Roaming"
  echo "- [task-6] OCPI implementation"
  echo "- [task-7] Roaming station listing"
  echo "- [task-8] Tariff and CDR exchange"
  echo ""
  echo "Phase 3: Smart Charging & Grid"
  echo "- [task-9] Real-time power balancing logic"
  echo "- [task-10] Smart grid interface"
  echo "- [task-11] V2G scheduling framework"
  echo ""
  echo "For more details, see TASK_MANAGEMENT.md"
}

get_next_task() {
  echo "Getting the next task to work on..."
  echo "Request ID: $REQUEST_ID"
  echo ""
  echo "Use the TaskManager to retrieve the next task:"
  echo "Tool: taskmanager.get_next_task"
  echo "Args: {\"requestId\": \"$REQUEST_ID\"}"
}

mark_task_done() {
  if [ -z "$1" ]; then
    echo "Error: Task ID is required"
    echo "Usage: ./task_manager.sh done [task-id]"
    echo "Example: ./task_manager.sh done task-1"
    return 1
  fi

  echo "Marking task $1 as done..."
  echo "Request ID: $REQUEST_ID"
  echo ""
  echo "Use the TaskManager to mark the task as done:"
  echo "Tool: taskmanager.mark_task_done"
  echo "Args: {\"requestId\": \"$REQUEST_ID\", \"taskId\": \"$1\"}"
}

approve_task() {
  if [ -z "$1" ]; then
    echo "Error: Task ID is required"
    echo "Usage: ./task_manager.sh approve [task-id]"
    echo "Example: ./task_manager.sh approve task-1"
    return 1
  fi

  echo "Approving task $1..."
  echo "Request ID: $REQUEST_ID"
  echo ""
  echo "Use the TaskManager to approve the task:"
  echo "Tool: taskmanager.approve_task_completion"
  echo "Args: {\"requestId\": \"$REQUEST_ID\", \"taskId\": \"$1\"}"
}

# Main script logic
case "$1" in
  list)
    list_tasks
    ;;
  next)
    get_next_task
    ;;
  done)
    mark_task_done "$2"
    ;;
  approve)
    approve_task "$2"
    ;;
  help|--help|-h)
    show_help
    ;;
  *)
    show_help
    ;;
esac